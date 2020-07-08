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

    public void analyse(final SortedSet<String> alfrescoVersions)
    {
        final Map<Conflict.Type, Map<String, Set<Conflict>>> conflictPerTypeAndResourceId = alfrescoVersions
            .stream()
            .flatMap(version -> warComparatorService.findConflicts(warInventoryStore.retrieve(version), version))
            .collect(groupingBy(
                Conflict::getType,
                () -> new EnumMap<>(Conflict.Type.class),
                groupingBy(
                    conflict -> conflict.getAmpResourceInConflict().getId(),
                    TreeMap::new,
                    toUnmodifiableSet())
            ));

        outputService.print(conflictPerTypeAndResourceId);
    }
}
