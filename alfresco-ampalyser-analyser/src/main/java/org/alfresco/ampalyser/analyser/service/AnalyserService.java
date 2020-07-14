/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.service;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableSortedSet;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.alfresco.ampalyser.analyser.checker.BeanOverwritingChecker.WHITELIST_BEAN_OVERRIDING;
import static org.alfresco.ampalyser.analyser.checker.BeanRestrictedClassesChecker.WHITELIST_BEAN_RESTRICTED_CLASSES;
import static org.alfresco.ampalyser.analyser.checker.Checker.ALFRESCO_VERSION;
import static org.alfresco.ampalyser.analyser.checker.FileOverwritingChecker.FILE_MAPPING_NAME;
import static org.alfresco.ampalyser.model.Resource.Type.FILE;
import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.alfresco.ampalyser.analyser.comparators.WarComparatorService;
import org.alfresco.ampalyser.analyser.parser.InventoryParser;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
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
    private WarComparatorService warComparatorService;

    @Autowired
    private AnalyserOutputService outputService;

    @Autowired
    private ObjectMapper objectMapper;

    public void analyse(final String ampPath)
    {
        analyse(ampPath,
            unmodifiableSortedSet(warInventoryStore.allKnownVersions()), 
            null,
            null, 
            null, 
            false);
    }

    public void analyse(final String ampPath, SortedSet<String> alfrescoVersions,
        final Set<String> warInventoryPaths, final String whitelistBeanOverridingPath,
        final String whitelistRestrictedClassesPath, final boolean verboseOutput)
    /**
     * Compares the extension with the WAR inventories of the requested Alfresco Versions and prints the results.
     *
     * @param alfrescoVersions
     */
    public void analyse(final SortedSet<String> alfrescoVersions)
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
     * Reads and loads {@link InventoryReport}s from a {@link Set} of .json files
     *
     * @return a {@link SortedSet} of the provided {@link InventoryReport}s
     */
    private SortedSet<InventoryReport> loadInventoryReports(Set<String> warInventoryPaths)
    {
        final SortedSet<InventoryReport> inventories = warInventoryPaths
            .stream()
            .map(this::retrieveInventory)
            .collect(toCollection(() -> new TreeSet<>(comparing(InventoryReport::getAlfrescoVersion))));

        return unmodifiableSortedSet(inventories);
    }

    private InventoryReport retrieveInventory(String path)
    {
        try
        {
            return inventoryParser.parseReport(new FileInputStream(path));
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to read inventory resource: " + path, e);
            throw new RuntimeException("Failed to read inventory resource: " + path, e);
        }
    }
}
