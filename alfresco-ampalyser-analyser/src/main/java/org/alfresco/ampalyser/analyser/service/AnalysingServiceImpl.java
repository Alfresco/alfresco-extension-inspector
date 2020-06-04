/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.service;

import java.io.IOException;

import org.alfresco.ampalyser.analyser.Analyser;
import org.alfresco.ampalyser.analyser.parser.InventoryParser;
import org.alfresco.ampalyser.inventory.service.InventoryService;
import org.alfresco.ampalyser.model.InventoryReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The backbone of the application. Triggers the workflow for the analysis process.
 *
 * @author Lucian Tuca
 */
@Service
public class AnalysingServiceImpl implements AnalysingService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysingServiceImpl.class);

    @Autowired
    private InventoryParser inventoryParser;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private Analyser analyser;

    private InventoryReport warInventoryReport;
    private InventoryReport ampInventoryReport;


    @Override
    public int analyse(String ampPath, String warInventoryReportPath)
    {
        // Load the report for the target war
        warInventoryReport = inventoryParser.parseReport(warInventoryReportPath);
        if (warInventoryReport == null)
        {
            // Exit with a code for parsing fail
            LOGGER.error("Failed to load war inventory report from file: " + warInventoryReportPath);
            return 1;
        }

        // Create the inventory for the source amp
        ampInventoryReport = inventoryService.extractInventoryReport(ampPath);
        if (ampInventoryReport == null)
        {
            // Exit with a code for parsing fail
            LOGGER.error("Failed to extract amp inventory report from file: " + ampPath);
            return 2;
        }

        try
        {
            analyser.startAnalysis(warInventoryReport, ampInventoryReport);
        }
        catch (IOException e)
        {

        }

        return 0;
    }
}
