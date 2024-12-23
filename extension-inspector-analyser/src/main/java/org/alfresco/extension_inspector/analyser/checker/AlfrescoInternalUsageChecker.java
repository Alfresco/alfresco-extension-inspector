/*
 * Copyright 2021 Alfresco Software, Ltd.
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
import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.alfresco.extension_inspector.analyser.checker.Checker.isInAllowedList;
import static org.alfresco.extension_inspector.model.Resource.Type.ALFRESCO_PUBLIC_API;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.alfresco.extension_inspector.analyser.result.AlfrescoInternalUsageConflict;
import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.service.ConfigService;
import org.alfresco.extension_inspector.analyser.service.ExtensionCodeAnalysisService;
import org.alfresco.extension_inspector.analyser.service.ExtensionResourceInfoService;
import org.alfresco.extension_inspector.model.AbstractResource;
import org.alfresco.extension_inspector.model.AlfrescoPublicApiResource;
import org.alfresco.extension_inspector.model.ClasspathElementResource;
import org.alfresco.extension_inspector.model.InventoryReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A checker/analyser designed to find all the class dependencies of a class within the .amp
 * and to report a list of conflicts for each of those classes.
 *
 * Each class that is found containing invalid dependencies (see {@link AlfrescoInternalUsageConflict}) is reported
 * as the original .amp resource
 *
 * @author Lucian Tuca
 */
@Component
public class AlfrescoInternalUsageChecker implements Checker
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoInternalUsageChecker.class);

    @Autowired
    private ConfigService configService;
    @Autowired
    private ExtensionResourceInfoService extensionResourceInfoService;
    @Autowired
    private ExtensionCodeAnalysisService extensionCodeAnalysisService;

    @Override
    public Stream<Conflict> processInternal(final InventoryReport warInventory, final String alfrescoVersion)
    {
        Set<String> allowedInternalClasses = configService.getInternalClassAllowedList();
        
        // Create a Map of the AlfrescoPublicApi with the class_id as the key and whether or not it is deprecated as the value
        final Map<String, Boolean> publicApis = warInventory
            .getResources().get(ALFRESCO_PUBLIC_API)
            .stream()
            .map(r -> (AlfrescoPublicApiResource) r)
            .collect(toUnmodifiableMap(
                AbstractResource::getId,
                AlfrescoPublicApiResource::isDeprecated
            ));

        final Map<String, Set<ClasspathElementResource>> extensionClassesById =
            extensionResourceInfoService.retrieveClasspathElementsById();

        // go through the AMP dependencies and search for conflicts
        return extensionCodeAnalysisService
            .retrieveDependenciesPerClass()
            .entrySet()
            .stream()
            // map to (class_name -> {alfresco_dependencies_not_marked_as_PublicAPI})
            .map(e -> entry(
                e.getKey(),
                e.getValue()
                    .stream()
                    .filter(d -> d.startsWith("/org/alfresco/")) // It is an Alfresco class
                    .filter(d -> !extensionClassesById.containsKey(d)) // Not defined inside the AMP
                    .filter(d -> !isInAllowedList(d, allowedInternalClasses)) // Not Allowed Internal Class
                    .map(d -> d.substring(1).replaceAll("/", ".").replace(".class", ""))
                    .filter(d -> (!publicApis.containsKey(d) || publicApis.get(d))) // Not PublicAPI or Deprecated_PublicAPI
                    .collect(toUnmodifiableSet())
            ))
            .filter(e -> !e.getValue().isEmpty()) // strip entries without invalid dependencies
            .flatMap(e -> extensionClassesById
                .getOrDefault(e.getKey(), emptySet()) // a class can be provided by multiple jars
                .stream()
                .map(r -> new AlfrescoInternalUsageConflict(
                    r,
                    e.getValue(),
                    alfrescoVersion
                )));
    }

    @Override
    public boolean canProcess(final InventoryReport warInventory, final String alfrescoVersion)
    {
        return true;
    }
}
