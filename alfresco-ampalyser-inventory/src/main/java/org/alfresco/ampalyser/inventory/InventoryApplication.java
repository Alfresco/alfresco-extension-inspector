package org.alfresco.ampalyser.inventory;

import org.alfresco.ampalyser.inventory.model.InventoryReport;
import org.alfresco.ampalyser.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InventoryApplication implements CommandLineRunner
{
    @Autowired
    private InventoryService inventoryService;

    public static void main(String[] args)
    {
        SpringApplication.run(InventoryApplication.class, args);
    }

    public void run(String[] args)
    {
        if (args.length != 1)
        {
            printUsage();
            return;
        }

        InventoryReport report = inventoryService.extractInventoryReport(args[0]);
    }

    private void printUsage()
    {
        System.out.println("usage:");
        System.out.println("java -jar alfresco-war-inventory.jar <alfresco-war-filename>");
    }
}
