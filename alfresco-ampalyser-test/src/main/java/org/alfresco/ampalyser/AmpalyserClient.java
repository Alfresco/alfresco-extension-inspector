/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.alfresco.ampalyser.command.InventoryCommand;
import org.alfresco.ampalyser.command.CommandReceiver;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.alfresco.ampalyser.models.CommandModel;
import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.util.JsonInventoryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AmpalyserClient
{
        @Autowired
        @Qualifier("inventoryCommand")
        private CommandModel inventoryCommand;

        @Autowired
        @Qualifier("analyserCommand")
        private CommandModel analyserCommand;

        @Autowired
        private CommandReceiver commReceiver;
        @Autowired
        private JsonInventoryParser jsonInventory;

        public CommandOutput runAmpalyserAnalyserCommand(List<String> cmdOptions)
        {
                return runCommand(cmdOptions, analyserCommand);
        }

        public CommandOutput runAmpalyserInventoryCommand(List<String> cmdOptions)
        {
                return runCommand(cmdOptions, inventoryCommand);
        }

        private CommandOutput runCommand(List<String> cmdOptions, CommandModel cmd)
        {
                List<String> commandOptions = new ArrayList<>(cmd.getCommandOptions());

                try
                {
                        // Add additional inventory command options
                        cmd.addCommandOptions(cmdOptions);
                        InventoryCommand invCmd = new InventoryCommand(commReceiver, cmd);

                        System.out.println("Running command: " + cmd.toString());
                        return invCmd.execute();
                }
                finally
                {
                        cmd.getCommandOptions().clear();
                        cmd.addCommandOptions(commandOptions);
                }
        }

        public Resource retrieveInventoryResource(Resource.Type resourceType, String resourceId, File jsonReport)
        {
                final InventoryReport inventoryReport = jsonInventory.getInventoryReportFromJson(jsonReport);

                return inventoryReport.getResources().get(resourceType)
                    .stream()
                    .filter(resource -> resource.getId().equals(resourceId))
                    .findFirst()
                    .orElse(null);
        }

        public Set<Resource> retrieveInventoryResources(Resource.Type resourceType, File jsonReport)
        {
                final InventoryReport inventoryReport = jsonInventory.getInventoryReportFromJson(jsonReport);

                return inventoryReport.getResources().get(resourceType);
        }
}
