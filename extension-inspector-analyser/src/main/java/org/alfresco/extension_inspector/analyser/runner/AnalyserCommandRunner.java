/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
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
