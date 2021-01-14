/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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
