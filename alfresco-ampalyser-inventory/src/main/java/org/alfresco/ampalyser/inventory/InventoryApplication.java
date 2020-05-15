/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory;

import org.alfresco.ampalyser.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InventoryApplication implements ApplicationRunner
{
    private static final String OUTPUT_ARG = "o";
    private static final String DEFAULT_REPORT_PATH = "inventory_report.json";

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
            printUsage();
            return;
        }
        String warPath = args.getNonOptionArgs().get(0);
        String reportPath = DEFAULT_REPORT_PATH;

        if (args.containsOption(OUTPUT_ARG) && !args.getOptionValues(OUTPUT_ARG).isEmpty())
        {
            String outputPath = args.getOptionValues(OUTPUT_ARG).get(0);
            if (outputPath.toLowerCase().endsWith("json"))
            {
                reportPath = outputPath;
            }
            else
            {
                printUsage();
                return;
            }
        }
        inventoryService.generateInventoryReport(warPath, reportPath);
    }

    private void printUsage()
    {
        System.out.println("Usage:");
        System.out.println("java -jar alfresco-ampalyser-inventory.jar <alfresco-war-filename> [--o=<report_file_path>.json]");
    }
}
