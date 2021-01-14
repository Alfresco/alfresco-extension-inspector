/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.alfresco.extension_inspector.analyser.checker;

import static java.util.Collections.emptySet;
import static java.util.Map.entry;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.alfresco.extension_inspector.analyser.checker.Checker.isInAllowedList;
import static org.alfresco.extension_inspector.model.Resource.Type.CLASSPATH_ELEMENT;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.result.WarLibraryUsageConflict;
import org.alfresco.extension_inspector.analyser.service.ConfigService;
import org.alfresco.extension_inspector.analyser.service.ExtensionCodeAnalysisService;
import org.alfresco.extension_inspector.analyser.service.ExtensionResourceInfoService;
import org.alfresco.extension_inspector.model.ClasspathElementResource;
import org.alfresco.extension_inspector.model.InventoryReport;
import org.alfresco.extension_inspector.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WarLibraryUsageChecker implements Checker
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WarLibraryUsageChecker.class);

    @Autowired
    private ExtensionResourceInfoService extensionResourceInfoService;
    @Autowired
    private ExtensionCodeAnalysisService extensionCodeAnalysisService;
    @Autowired
    private ConfigService configService;

    @Override
    public Stream<Conflict> processInternal(final InventoryReport warInventory, final String alfrescoVersion)
    {
        final Set<String> allExtensionDependencies = extensionCodeAnalysisService.retrieveAllDependencies();
        final Set<String> thirdPartyAllowedList = configService.getThirdPartyAllowedList();

        // Iterate through the WAR classpath elements and keep the ones that could be dependencies of the extension.
        // We keep this intermediate data structure (Set), so that we don't hash the entire War inventory
        final Map<String, Set<Resource>> resourcesInWar = warInventory
            .getResources().getOrDefault(CLASSPATH_ELEMENT, emptySet())
            .stream()
            .filter(s -> s.getId().endsWith(".class"))
            .filter(s -> !s.getId().startsWith("/org/alfresco/")) // strip Alfresco Classes
            .filter(s -> !s.getId().startsWith("/javax/")) // strip JavaX Classes
            .filter(s -> allExtensionDependencies.contains(s.getId())) // keep if the WAR entry could be a dependency of the extension
            .collect(groupingBy(Resource::getId,toUnmodifiableSet()));
        final Set<String> classesInWar = resourcesInWar.keySet();
        
        final Map<String, Set<ClasspathElementResource>> extensionClassesById =
            extensionResourceInfoService.retrieveClasspathElementsById();

        // now we can go back through the AMP dependencies and search for conflicts
        return extensionCodeAnalysisService
            .retrieveDependenciesPerClass()
            .entrySet()
            .stream()
            // map to (class_name -> {dependencies_only_present_int_the_WAR})
            .map(e -> entry(
                e.getKey(),
                e.getValue()
                 .stream()
                 .filter(c -> !isInAllowedList(c, thirdPartyAllowedList))
                 .filter(d -> !extensionClassesById.containsKey(d)) // dependencies not provided in the extension
                 .filter(classesInWar::contains) // dependencies provided by the WAR
                 .collect(toUnmodifiableSet())
            ))
            .filter(e -> !e.getValue().isEmpty()) // strip entries without invalid dependencies
            .flatMap(e -> extensionClassesById
                .getOrDefault(e.getKey(), emptySet()) // a class can be provided by multiple jars
                .stream()
                .map(r -> new WarLibraryUsageConflict(
                    r,
                    e.getValue()
                        .stream()
                        .flatMap(s->resourcesInWar.get(s).stream())
                        .collect(toUnmodifiableSet()),
                    alfrescoVersion
                ))
                .filter(c -> !c.getDependencies().isEmpty())
            );

        // TODO: create conflicts for extension dependencies not satisfied by either the AMP or the WAR libraries
    }

    @Override
    public boolean canProcess(final InventoryReport warInventory, final String alfrescoVersion)
    {
        return !extensionCodeAnalysisService.retrieveDependenciesPerClass().isEmpty();
    }
}
