/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.service;

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

import org.alfresco.ampalyser.inventory.EntryProcessor;
import org.alfresco.ampalyser.model.FileResource;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
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
