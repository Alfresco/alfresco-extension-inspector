/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.extension_inspector.inventory;

import java.io.File;

import org.alfresco.extension_inspector.inventory.output.InventoryOutput;
import org.alfresco.extension_inspector.inventory.output.JSONInventoryOutput;
import org.alfresco.extension_inspector.inventory.service.InventoryService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryApplication implements ApplicationRunner, ExitCodeGenerator
{
    private static final Logger logger = LoggerFactory.getLogger(InventoryApplication.class);

    private static final String OUTPUT_ARG = "o";

    private static final int EXIT_CODE_EXCEPTION = 1;

    @Autowired
    private InventoryService inventoryService;

    private int exitCode = 0;

    public static void main(String[] args)
    {
        System.exit(SpringApplication.exit(SpringApplication.run(InventoryApplication.class, args)));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        if (args.getNonOptionArgs().isEmpty())
        {
            logger.error("Missing war file.");
            printUsage();
            setExceptionExitCode();
            return;
        }
        String warPath = args.getNonOptionArgs().get(0);
        if (!isWarValid(warPath))
        {
            logger.error("The war file is not valid.");
            printUsage();
            setExceptionExitCode();
            return;
        }

        String reportPath = getOutputReportPath(args, warPath);
        // TODO: Make it a bean and inject it?
        InventoryOutput output = new JSONInventoryOutput(warPath, reportPath);
        inventoryService.generateInventoryReport(warPath, output);
    }

    private static boolean isWarValid(String warPath)
    {
        return FilenameUtils.getExtension(warPath).equalsIgnoreCase("war") &&
                new File(warPath).exists();
    }

    private static String getOutputReportPath(ApplicationArguments args, String warPath) throws Exception
    {
        return args.containsOption(OUTPUT_ARG) && !args.getOptionValues(OUTPUT_ARG).isEmpty() ?
                args.getOptionValues(OUTPUT_ARG).get(0) : "";
    }

    private static void printUsage()
    {
        System.out.println("Usage:");
        System.out.println("java -jar alfresco-ampalyser-inventory.jar <alfresco-war-filename> [--o=<report_file_path>.json]");
    }

    @Bean
    ExitCodeExceptionMapper exitCodeToExceptionMapper()
    {
        return exception -> {
            // Set specific exit codes based on the exception type

            // Default exit code
            return EXIT_CODE_EXCEPTION;
        };
    }

    /**
     * @return the code 1 if an exception occurs. Otherwise, on a clean exit, it
     *         provides 0 as the exit code.
     */
    @Override
    public int getExitCode()
    {
        return exitCode;
    }

    /**
     * Set the exit code to default exception exit code.
     */
    private void setExceptionExitCode()
    {
        this.exitCode = EXIT_CODE_EXCEPTION;
    }
}
