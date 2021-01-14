/*
 * Copyright 2021 Alfresco Software, Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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

        public CommandOutput runExtensionInspectorAnalyserCommand(List<String> cmdOptions)
        {
                return runAnalyserCommand(cmdOptions, cmdAnalyser);
        }

        public CommandOutput runExtensionInspectorInventoryCommand(List<String> cmdOptions)
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
