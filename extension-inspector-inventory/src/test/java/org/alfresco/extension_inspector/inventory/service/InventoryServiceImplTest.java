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

package org.alfresco.extension_inspector.inventory.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.extension_inspector.inventory.EntryProcessor;
import org.alfresco.extension_inspector.model.FileResource;
import org.alfresco.extension_inspector.model.InventoryReport;
import org.alfresco.extension_inspector.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest
{
    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImplTest.class);

    @Mock
    private EntryProcessor entryProcessor;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @Test
    public void testWhenWarCannotBeOpenedThenIAExceptionShouldBeThrown()
    {
        try
        {
            inventoryService.extractInventoryReport("no-war-here");
            fail("Processing should fail with IllegalArgumentException when war cannot be found at given path.");
        }
        catch (IllegalArgumentException e)
        {
            logger.info("Expected exception when war cannot be found at given path.");
        }
    }

    @Test
    public void testExtractEmptyInventoryReportWhenWarIsEmpty() throws FileNotFoundException
    {
        String warPath = ResourceUtils.getFile("classpath:test-empty.war").getPath();
        InventoryReport report = inventoryService.extractInventoryReport(warPath);
        assertTrue(report.getResources().isEmpty());
    }

    @Test
    public void testExtractInventoryReportFromValidWar() throws IOException
    {
        Set<Resource> resourceList = new LinkedHashSet<>();
        resourceList.add(new FileResource("file.txt", "file.txt"));
        Map<Resource.Type, Set<Resource>> resources = Map.of(Resource.Type.FILE, resourceList);
        when(entryProcessor.processWarEntry(any(), any())).thenReturn(resources);

        String warPath = ResourceUtils.getFile("classpath:test.war").getPath();
        InventoryReport report = inventoryService.extractInventoryReport(warPath);
        assertTrue(!report.getResources().isEmpty());
        assertTrue(!report.getResources().get(Resource.Type.FILE).isEmpty());
        verify(entryProcessor, times(3)).processWarEntry(any(), any());
    }

}
