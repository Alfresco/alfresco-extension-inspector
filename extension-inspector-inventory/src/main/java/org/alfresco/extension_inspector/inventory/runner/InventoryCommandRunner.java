package org.alfresco.extension_inspector.inventory.runner;

import static org.alfresco.extension_inspector.usage.UsagePrinter.printInventoryUsage;

import java.io.File;

import org.alfresco.extension_inspector.inventory.output.InventoryOutput;
import org.alfresco.extension_inspector.inventory.output.JSONInventoryOutput;
import org.alfresco.extension_inspector.inventory.service.InventoryService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

@Service
public class InventoryCommandRunner
{
    private static final String OUTPUT_ARG = "o";

    @Autowired
    private InventoryService inventoryService;

    public void execute(final ApplicationArguments args)
    {
        if (args.getNonOptionArgs().isEmpty())
        {
            printInventoryUsage("Missing war file.");
            throw new IllegalArgumentException();
        }
        final String warPath = args.getNonOptionArgs().get(0);
        if (!isWarValid(warPath))
        {
            printInventoryUsage("The war file is not valid.");
            throw new IllegalArgumentException();
        }

        final String reportPath = getOutputReportPath(args, warPath);
        // TODO: Make it a bean and inject it?
        final InventoryOutput output = new JSONInventoryOutput(warPath, reportPath);

        inventoryService.generateInventoryReport(warPath, output);
    }

    private static boolean isWarValid(String warPath)
    {
        return FilenameUtils.getExtension(warPath).equalsIgnoreCase("war") &&
               new File(warPath).exists();
    }

    private static String getOutputReportPath(ApplicationArguments args, String warPath)
    {
        return args.containsOption(OUTPUT_ARG) && !args.getOptionValues(OUTPUT_ARG).isEmpty() ?
               args.getOptionValues(OUTPUT_ARG).get(0) : "";
    }
}
