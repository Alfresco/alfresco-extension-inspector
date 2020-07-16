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

import org.alfresco.ampalyser.command.CommandAnalyserImpl;
import org.alfresco.ampalyser.command.CommandExecutor;
import org.alfresco.ampalyser.command.CommandInventoryImpl;
import org.alfresco.ampalyser.command.CommandReceiver;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.alfresco.ampalyser.models.AnalyserCommand;
import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.models.InventoryCommand;
import org.alfresco.ampalyser.util.JsonInventoryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AmpalyserClient
{
        @Autowired
        private InventoryCommand cmd;
        @Autowired
        private AnalyserCommand cmdAnalyser;
        @Autowired
        private CommandReceiver commReceiver;
        @Autowired
        private CommandExecutor executor;
        @Autowired
        private JsonInventoryParser jsonInventory;

        public CommandOutput runInventoryAnalyserCommand(List<String> cmdOptions)
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

        public CommandOutput runAnalyserCommand(List<String> cmdOptions)
        {
                CommandOutput cmdOut;
                List<String> commandOptions = new ArrayList<>(cmdAnalyser.getCommandOptions());

                try
                {
                        // Add additional inventory command options
                        cmdAnalyser.addCommandOptions(cmdOptions);
                        CommandAnalyserImpl analyserCmd = new CommandAnalyserImpl(commReceiver, cmdAnalyser);

                        System.out.println("Running command: " + cmdAnalyser.toString());
                        cmdOut = executor.execute(analyserCmd);
                }
                finally
                {
                        cmdAnalyser.getCommandOptions().clear();
                        cmdAnalyser.addCommandOptions(commandOptions);
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
