/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.alfresco.ampalyser.model.InventoryReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyserService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyserService.class);

    @Autowired
    private ConfigService configService;
    @Autowired
    private WarInventoryReportStore warInventoryStore;
    @Autowired
    private InventoryLoaderService inventoryLoaderService;
    @Autowired
    private WarComparatorService warComparatorService;
    @Autowired
    private AnalyserOutputService outputService;

    /**
     * Compares the extension with the WAR inventories of the requested Alfresco Versions and prints the results.
     *
     * @param alfrescoVersions
     */
    public void analyseAgainstKnownVersions(final SortedSet<String> alfrescoVersions)
    {
        // The outputService needs the results (Conflicts) grouped by their type and then by their resource IDs
        final Map<Conflict.Type, Map<String, Set<Conflict>>> conflictPerTypeAndResourceId = alfrescoVersions
            .stream()
            // for each WAR version call the warComparatorService (which in turn calls the Checkers)
            .flatMap(version -> warComparatorService.findConflicts(warInventoryStore.retrieve(version), version))
            // group the found conflicts first by their type => Map<Conflict.Type, ...>
            .collect(groupingBy(
                Conflict::getType,
                () -> new EnumMap<>(Conflict.Type.class), // use EnumMaps - they're fast & ordered
                // then group the conflicts by their resource ID => Map<String, ...>
                groupingBy(
                    conflict -> conflict.getAmpResourceInConflict().getId(),
                    TreeMap::new, // use an ordered collection
                    toUnmodifiableSet()) // => Set<Conflict>
            ));

        outputService.print(conflictPerTypeAndResourceId);
    }

    /**
     * Compares the extension with the provided WAR inventories and prints the results.
     *
     * @param warInventoryPaths
     */
    public void analyseAgainstWarInventories(final Set<String> warInventoryPaths)
    {
        final Map<String, InventoryReport> warInventories = inventoryLoaderService.loadInventoryReports(warInventoryPaths);

        // The outputService needs the results (Conflicts) grouped by their type and then by their resource IDs
        final Map<Conflict.Type, Map<String, Set<Conflict>>> conflictPerTypeAndResourceId = warInventories
            .entrySet()
            .stream()
            // for each WAR version call the warComparatorService (which in turn calls the Checkers)
            .flatMap(e -> warComparatorService.findConflicts(e.getValue(), e.getKey()))
            // group the found conflicts first by their type => Map<Conflict.Type, ...>
            .collect(groupingBy(
                Conflict::getType,
                () -> new EnumMap<>(Conflict.Type.class), // use EnumMaps - they're fast & ordered
                // then group the conflicts by their resource ID => Map<String, ...>
                groupingBy(
                    conflict -> conflict.getAmpResourceInConflict().getId(),
                    TreeMap::new, // use an ordered collection
                    toUnmodifiableSet()) // => Set<Conflict>
            ));

        outputService.print(conflictPerTypeAndResourceId);
    }
}
