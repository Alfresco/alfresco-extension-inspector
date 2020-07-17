/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser;

import org.alfresco.ampalyser.command.CommandExecutor;
import org.alfresco.ampalyser.command.CommandInventoryImpl;
import org.alfresco.ampalyser.command.CommandReceiver;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.alfresco.ampalyser.models.CommandModel;
import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.util.JsonInventoryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class AmpalyserClient
{
        @Autowired
        @Qualifier("initInventoryCommand")
        private CommandModel cmdInventory;

        @Autowired
        @Qualifier("initAnalyserCommand")
        private CommandModel cmdAnalyser;

        @Autowired
        private CommandReceiver commReceiver;
        @Autowired
        private CommandExecutor executor;
        @Autowired
        private JsonInventoryParser jsonInventory;

        public CommandOutput runAmpalyserAnalyserCommand(List<String> cmdOptions)
        {
                return runAnalyserCommand(cmdOptions, cmdAnalyser);
        }

        public CommandOutput runAmpalyserInventoryCommand(List<String> cmdOptions)
        {
                return runAnalyserCommand(cmdOptions, cmdInventory);
        }

        private CommandOutput runAnalyserCommand(List<String> cmdOptions, CommandModel cmd)
        {
                CommandOutput cmdOut;
                List<String> commandOptions = new ArrayList<>(cmd.getCommandOptions());

                try
                {
                        // Add additional inventory command options
                        cmd.addCommandOptions(cmdOptions);
                        CommandInventoryImpl invCmd = new CommandInventoryImpl(commReceiver, cmd);

                        System.out.println("Running command: " + cmd.toString());
                        cmdOut = executor.execute(invCmd);
                }
                finally
                {
                        cmd.getCommandOptions().clear();
                        cmd.addCommandOptions(commandOptions);
                }

                return cmdOut;
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

        public List<Resource> retrieveInventoryResources(Resource.Type resourceType, File jsonReport)
        {
                final InventoryReport inventoryReport = jsonInventory.getInventoryReportFromJson(jsonReport);

                return inventoryReport.getResources().get(resourceType);
        }
}
