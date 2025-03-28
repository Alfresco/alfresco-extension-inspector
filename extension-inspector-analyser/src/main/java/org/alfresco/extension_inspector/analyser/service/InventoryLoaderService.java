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

import static java.util.Collections.unmodifiableSortedMap;
import static java.util.stream.Collectors.toMap;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

import org.alfresco.extension_inspector.analyser.parser.InventoryParser;
import org.alfresco.extension_inspector.model.InventoryReport;
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
     * @return a {@link Map} of (alfrescoVersion -> InventoryReport)
     */
    public SortedMap<String, InventoryReport> loadInventoryReports(final Set<String> warInventoryPaths)
    {
        final SortedMap<String, InventoryReport> map = warInventoryPaths
            .stream()
            .map(this::retrieveInventory)
            .collect(toMap(
                InventoryReport::getAlfrescoVersion,
                Function.identity(),
                (a, b) -> b,
                TreeMap::new
            ));
        return unmodifiableSortedMap(map);
    }

    private InventoryReport retrieveInventory(final String path)
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
