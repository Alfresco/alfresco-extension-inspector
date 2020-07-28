/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Map.entry;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.alfresco.ampalyser.model.Resource.Type.CLASSPATH_ELEMENT;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.WarLibraryUsageConflict;
import org.alfresco.ampalyser.analyser.service.ExtensionCodeAnalysisService;
import org.alfresco.ampalyser.analyser.service.ExtensionResourceInfoService;
import org.alfresco.ampalyser.model.ClasspathElementResource;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
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

    @Override
    public Stream<Conflict> processInternal(final InventoryReport warInventory, final String alfrescoVersion)
    {
        final Set<String> allExtensionDependencies = extensionCodeAnalysisService.retrieveAllDependencies();

        // Iterate through the WAR classpath elements and keep the ones that could be dependencies of the extension.
        // We keep this intermediate data structure (Set), so that we don't hash the entire War inventory
        final Set<String> classesInWar = warInventory
            .getResources().getOrDefault(CLASSPATH_ELEMENT, emptyList())
            .stream()
            .map(Resource::getId)
            .filter(s -> s.endsWith(".class"))
            .filter(s -> !s.startsWith("/org/alfresco/")) // strip Alfresco Classes
            .filter(allExtensionDependencies::contains) // keep if the WAR entry could be a dependency of the extension
            .collect(toUnmodifiableSet());

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
                    e.getValue(),
                    alfrescoVersion
                )));
    }

    @Override
    public boolean canProcess(final InventoryReport warInventory, final String alfrescoVersion)
    {
        return !extensionCodeAnalysisService.retrieveDependenciesPerClass().isEmpty();
    }
}
