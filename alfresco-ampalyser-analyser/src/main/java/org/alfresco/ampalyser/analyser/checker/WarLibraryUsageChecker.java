/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.AbstractMap.SimpleEntry;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.alfresco.ampalyser.model.Resource.Type.CLASSPATH_ELEMENT;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.WarLibraryUsageConflict;
import org.alfresco.ampalyser.analyser.service.ConfigService;
import org.alfresco.ampalyser.analyser.service.ExtensionResourceInfoService;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WarLibraryUsageChecker implements Checker
{
    @Autowired
    private ConfigService configService;
    @Autowired
    private ExtensionResourceInfoService extensionResourceInfoService;

    @Override
    public Stream<Conflict> processInternal(final InventoryReport warInventory, final String alfrescoVersion)
    {
        final Map<String, Set<Resource>> extensionClassesByName = extensionResourceInfoService.retrieveClassResourcesByName();
        final Set<String> extensionDependencies = extensionResourceInfoService.retrieveAllDependencies();

        // Iterate through the WAR classpath elements and keep the ones that could be dependencies of the extension
        final Set<String> dependenciesInWar = warInventory
            .getResources().getOrDefault(CLASSPATH_ELEMENT, emptyList())
            .stream()
            .map(Resource::getId)
            .filter(s -> s.endsWith(".class"))
            .filter(s -> !s.startsWith("/org/alfresco/"))
            .map(s -> s.substring(1)) //.replaceAll("/", ".")) todo?
            .filter(extensionDependencies::contains)
            .collect(toUnmodifiableSet());

        return extensionResourceInfoService
            .retrieveDependenciesPerClass()
            .entrySet()
            .stream()
            .map(e -> new SimpleEntry<>(
                e.getKey(),
                e.getValue()
                 .stream()
                 // filter dependencies included in the extension
                 .filter(d -> !extensionClassesByName.containsKey(d))
                 .filter(dependenciesInWar::contains)
                 .collect(toUnmodifiableSet())
            ))
            .filter(e -> !e.getValue().isEmpty())
            .flatMap(e -> extensionClassesByName
                .get(e.getKey())
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
        return !extensionResourceInfoService.retrieveDependenciesPerClass().isEmpty();
    }
}
