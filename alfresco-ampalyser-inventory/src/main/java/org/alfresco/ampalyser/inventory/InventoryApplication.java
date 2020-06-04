/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory;

import java.io.File;

import org.alfresco.ampalyser.inventory.output.InventoryOutput;
import org.alfresco.ampalyser.inventory.output.JSONInventoryOutput;
import org.alfresco.ampalyser.inventory.service.InventoryService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InventoryApplication implements ApplicationRunner
{
    private static final Logger logger = LoggerFactory.getLogger(InventoryApplication.class);

    private static final String OUTPUT_ARG = "o";

    @Autowired
    private InventoryService inventoryService;

    public static void main(String[] args)
    {
        SpringApplication.run(InventoryApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        if (args.getNonOptionArgs().isEmpty())
        {
            logger.error("Missing war file.");
            printUsage();
            return;
        }
        String warPath = args.getNonOptionArgs().get(0);
        if (!isWarValid(warPath))
        {
            logger.error("The war file is not valid.");
            printUsage();
            return;
        }

        try
        {
            String reportPath = getOutputReportPath(args, warPath);
            // TODO: Make it a bean and inject it?
            InventoryOutput output = new JSONInventoryOutput(warPath, reportPath);
            inventoryService.generateInventoryReport(warPath, output);
        }
        catch (Exception e)
        {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private boolean isWarValid(String warPath)
    {
        return FilenameUtils.getExtension(warPath).equalsIgnoreCase("war") &&
                new File(warPath).exists();
    }

    private String getOutputReportPath(ApplicationArguments args, String warPath) throws Exception
    {
        return args.containsOption(OUTPUT_ARG) && !args.getOptionValues(OUTPUT_ARG).isEmpty() ?
                args.getOptionValues(OUTPUT_ARG).get(0) : "";
    }

    private void printUsage()
    {
        System.out.println("Usage:");
        System.out.println("java -jar alfresco-ampalyser-inventory.jar <alfresco-war-filename> [--o=<report_file_path>.json]");
    }
}
