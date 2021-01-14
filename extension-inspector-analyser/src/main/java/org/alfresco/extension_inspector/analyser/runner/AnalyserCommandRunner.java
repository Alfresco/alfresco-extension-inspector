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

package org.alfresco.extension_inspector.analyser.runner;

import static org.alfresco.extension_inspector.analyser.runner.CommandOptionsResolver.extractExtensionPath;
import static org.alfresco.extension_inspector.analyser.runner.CommandOptionsResolver.extractWarInventoryPaths;
import static org.alfresco.extension_inspector.analyser.runner.CommandOptionsResolver.isVerboseOutput;
import static org.alfresco.extension_inspector.analyser.runner.CommandOptionsResolver.validateAnalyserOptions;
import static org.alfresco.extension_inspector.usage.UsagePrinter.printAnalyserUsage;

import java.util.Set;
import java.util.SortedSet;

import org.alfresco.extension_inspector.analyser.service.AnalyserService;
import org.alfresco.extension_inspector.analyser.service.ConfigService;
import org.alfresco.extension_inspector.analyser.store.WarInventoryReportStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

@Service
public class AnalyserCommandRunner
{
    @Autowired
    private ConfigService configService;
    @Autowired
    private WarInventoryReportStore warInventoryReportStore;
    @Autowired
    private AnalyserService analyserService;
    @Autowired
    private CommandOptionsResolver commandOptionsResolver;

    public void execute(ApplicationArguments args)
    {
        // Stop if no extension file have been provided
        if (args.getNonOptionArgs().isEmpty())
        {
            printAnalyserUsage("Missing extension file.");
            throw new IllegalArgumentException();
        }

        executeExtensionAnalysis(args);
    }

    private void executeExtensionAnalysis(ApplicationArguments args)
    {
        final String extensionPath = extractExtensionPath(args.getNonOptionArgs());

        validateAnalyserOptions(args.getOptionNames());

        configService.setVerboseOutput(isVerboseOutput(args));

        configService.registerExtensionPath(extensionPath);
        
        // retrieve provided war inventories, if any
        final Set<String> warInventories = extractWarInventoryPaths(args);
        if (warInventories != null)
        {
            analyserService.analyseAgainstWarInventories(warInventories);
            return;
        }

        // no war inventories provided
        // check TARGET_VERSION option
        final SortedSet<String> versions = commandOptionsResolver.extractTargetVersions(args);
        analyserService.analyseAgainstKnownVersions(versions);
    }

    public void listKnownAlfrescoVersions()
    {
        System.out.println("Known Alfresco versions: " + warInventoryReportStore.allKnownVersions());
    }
}
