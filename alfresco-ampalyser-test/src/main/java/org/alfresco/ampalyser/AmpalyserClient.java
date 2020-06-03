package org.alfresco.ampalyser;

import org.alfresco.ampalyser.command.CommandExecutor;
import org.alfresco.ampalyser.command.CommandImpl;
import org.alfresco.ampalyser.command.CommandReceiver;
import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.models.InventoryCommand;
import org.alfresco.ampalyser.models.InventoryReport;
import org.alfresco.ampalyser.models.Resource;
import org.alfresco.ampalyser.util.JsonInventoryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class AmpalyserClient
{
        @Autowired
        private InventoryCommand cmd;
        @Autowired
        private CommandReceiver commReceiver;
        @Autowired
        private CommandExecutor executor;
        @Autowired
        private JsonInventoryParser jsonInventory;

        public CommandOutput runInventoryAnalyserCommand(List<String> cmdOptions)
        {
                // Add additional inventory command options
                cmd.addCommandOptions(cmdOptions);
                CommandImpl invCmd = new CommandImpl(commReceiver, cmd);

                System.out.println("Running command: " + cmd.toString());
                CommandOutput cmdOut = executor.execute(invCmd);

                return cmdOut;
        }

        public Resource retrieveInventoryResource(Resource.Type resourceType, String resourceId, File jsonReport)
        {
                InventoryReport inventoryReport = jsonInventory.getInventoryReportFromJson(jsonReport);

                return inventoryReport.getResource(resourceType, resourceId);
        }

        public List<Resource> retrieveInventoryResources(Resource.Type resourceType, File jsonReport)
        {
                InventoryReport inventoryReport = jsonInventory.getInventoryReportFromJson(jsonReport);

                return inventoryReport.getResources(resourceType);
        }
}
