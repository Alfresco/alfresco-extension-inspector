/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.runner;

import static org.alfresco.ampalyser.analyser.runner.CommandOptionsResolver.extractExtensionPath;
import static org.alfresco.ampalyser.analyser.runner.CommandOptionsResolver.extractWhitelistPath;
import static org.alfresco.ampalyser.analyser.runner.CommandOptionsResolver.validateAnalyserOptions;
import static org.alfresco.ampalyser.analyser.runner.CommandOptionsResolver.validateOptionsForCommand;
import static org.alfresco.ampalyser.analyser.usage.UsagePrinter.printHelp;
import static org.alfresco.ampalyser.analyser.usage.UsagePrinter.printUsage;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import org.alfresco.ampalyser.analyser.service.AnalyserService;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

@Service
public class CommandRunner
{
    @Autowired
    private WarInventoryReportStore warInventoryReportStore;
    @Autowired
    private AnalyserService analyserService;
    @Autowired
    private CommandOptionsResolver commandOptionsResolver;
    
    public void executeCommand(String command, Iterator<String> commandOptions)
    {
        switch (command)
        {
        case "help":
            validateOptionsForCommand("--help", commandOptions);
            printHelp();
            break;
        case "list-known-alfresco-versions":
            validateOptionsForCommand("--list-known-alfresco-versions", commandOptions);
            listKnownAlfrescoVersions();
            break;
        default:
            System.out.println("error: unknown command provided: " + command);
            printUsage("[--help]", "[--list-known-alfresco-versions]",
                "<extension-filename> [--target=6.1.0[-7.0.0]] "
                    + "[--whitelistBeanOverriding=/path/to/bean_overriding_whitelist.json] "
                    + "[--whitelistBeanRestrictedClasses=/path/to/bean_restricted_classes_whitelist.json] "
                    + "[--verbose]");
            throw new IllegalArgumentException();
        }
    }

    public void executeExtensionAnalysis(ApplicationArguments args)
    {
        final String extensionPath = extractExtensionPath(args.getNonOptionArgs());
        
        final Set<String> options = args.getOptionNames();
        if (options.isEmpty())
        {
            analyserService.analyse(extensionPath);
            return;
        }
        validateAnalyserOptions(options);

        final SortedSet<String> versions = commandOptionsResolver
            .extractTargetVersions(args);
        final String beanOverridingWhitelistPath = extractWhitelistPath(
            "whitelistBeanOverriding", args);
        final String beanRestrictedClassWhitelistPath = extractWhitelistPath(
            "whitelistBeanRestrictedClasses", args);

        analyserService.analyse(extensionPath, versions, beanOverridingWhitelistPath,
            beanRestrictedClassWhitelistPath, args.containsOption("verbose"));
    }

    private void listKnownAlfrescoVersions()
    {
        System.out.println("Known Alfresco versions: " + warInventoryReportStore.allKnownVersions());
    }

}
