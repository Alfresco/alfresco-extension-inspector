/*
 * Copyright 2023 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.extension_inspector.analyser.checker;

import static java.util.Collections.emptySet;
import static java.util.Map.entry;
import static java.util.stream.Collectors.toUnmodifiableSet;

import static org.alfresco.extension_inspector.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.result.JakartaMigrationConflict;
import org.alfresco.extension_inspector.analyser.service.ConfigService;
import org.alfresco.extension_inspector.analyser.service.ExtensionCodeAnalysisService;
import org.alfresco.extension_inspector.analyser.service.ExtensionResourceInfoService;
import org.alfresco.extension_inspector.model.ClasspathElementResource;
import org.alfresco.extension_inspector.model.InventoryReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JakartaMigrationChecker implements Checker
{
    @Autowired
    private ConfigService configService;
    @Autowired
    private ExtensionResourceInfoService extensionResourceInfoService;
    @Autowired
    private ExtensionCodeAnalysisService extensionCodeAnalysisService;

    @Override
    public Stream<Conflict> processInternal(final InventoryReport warInventory, final String alfrescoVersion)
    {
        final Set<String> jakartaMigrationClassList = configService.getJakartaMigrationClassList();

        Set<String> inventoryAcsClasspathElements = warInventory.getResources().getOrDefault(CLASSPATH_ELEMENT, emptySet())
                .stream()
                .map(r -> r.getId())
                .filter(s -> Checker.isInAllowedList(s, jakartaMigrationClassList))
                .map(c -> c.substring(1).replace(".class", ""))
                .collect(Collectors.toSet());

        final Set<String> acsClasspathElements = adjustForProvidedDependencies(inventoryAcsClasspathElements);

        final Map<String, Set<ClasspathElementResource>> extensionClassesById =
                extensionResourceInfoService.retrieveClasspathElementsById();

        return extensionCodeAnalysisService
                .retrieveDependenciesPerClass()
                .entrySet()
                .stream()
                // map to (class_name -> {alfresco_dependencies})
                .map(e -> entry(
                        e.getKey(),
                        e.getValue()
                         .stream()
                         .filter(d -> !extensionClassesById.containsKey(d)) // Not defined inside the AMP
                         .filter(d -> Checker.isInAllowedList(d, jakartaMigrationClassList))
                         .filter(d -> !Checker.isInAllowedList(d, acsClasspathElements))
                         .map(d -> d.substring(1).replaceAll("/", ".").replace(".class", ""))
                         .collect(toUnmodifiableSet())
                               ))
                .filter(e -> !e.getValue().isEmpty()) // strip entries without invalid dependencies
                .flatMap(e -> extensionClassesById
                        .getOrDefault(e.getKey(), emptySet()) // a class can be provided by multiple jars
                        .stream()
                        .map(r -> new JakartaMigrationConflict(
                                r,
                                e.getValue(),
                                alfrescoVersion
                        )));
    }

    @Override
    public boolean canProcess(final InventoryReport warInventory, final String alfrescoVersion)
    {
        return !configService.getExtensionResources(CLASSPATH_ELEMENT).isEmpty() &&
                !isEmpty(warInventory.getResources().get(CLASSPATH_ELEMENT));
    }

    /* Provided dependencies are not included in the ACS inventory report, which causes javax/jakarta servlet usage to
       be flagged as a conflict. To prevent this an assumption is made that if javax.mail or jakarta.mail exists in the
       inventory then the corresponding javax.servlet or jakarta.servlet can also be added to the acs inventory.
     */
    private Set<String> adjustForProvidedDependencies(Set<String> classpathElements)
    {
        if (isLibraryInClasspathElements("javax/mail/", classpathElements))
        {
            classpathElements.add("javax/servlet");
        }
        if (isLibraryInClasspathElements("jakarta/mail/", classpathElements))
        {
            classpathElements.add("jakarta/servlet");
        }
        return classpathElements;
    }

    private static boolean isLibraryInClasspathElements(String libraryName, Set<String> elements)
    {
        return elements.stream().anyMatch(s -> s.startsWith(libraryName));
    }
}
