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
package org.alfresco.extension_inspector.analyser.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.store.WarInventoryReportStore;
import org.alfresco.extension_inspector.model.InventoryReport;
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
