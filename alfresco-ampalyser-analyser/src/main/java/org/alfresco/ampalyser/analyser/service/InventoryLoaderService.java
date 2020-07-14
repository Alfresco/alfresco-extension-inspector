/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.service;

import static java.util.Collections.unmodifiableSortedSet;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.alfresco.ampalyser.analyser.parser.InventoryParser;
import org.alfresco.ampalyser.model.InventoryReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryLoaderService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryLoaderService.class);

    @Autowired
    private InventoryParser inventoryParser;
    
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
