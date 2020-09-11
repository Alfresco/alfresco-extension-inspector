/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.extension_inspector;

import org.alfresco.extension_inspector.command.CommandExecutor;
import org.alfresco.extension_inspector.command.CommandInventoryImpl;
import org.alfresco.extension_inspector.command.CommandReceiver;
import org.alfresco.extension_inspector.model.InventoryReport;
import org.alfresco.extension_inspector.model.Resource;
import org.alfresco.extension_inspector.models.CommandModel;
import org.alfresco.extension_inspector.models.CommandOutput;
import org.alfresco.extension_inspector.util.JsonInventoryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ExtensionInspectorClient
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

        public Set<Resource> retrieveInventoryResources(Resource.Type resourceType, File jsonReport)
        {
                final InventoryReport inventoryReport = jsonInventory.getInventoryReportFromJson(jsonReport);

                return inventoryReport.getResources().get(resourceType);
        }
}
