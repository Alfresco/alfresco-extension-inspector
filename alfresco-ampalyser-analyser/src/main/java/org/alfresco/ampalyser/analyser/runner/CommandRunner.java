/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.runner;

import static org.alfresco.ampalyser.analyser.runner.CommandOptionsResolver.WHITELIST_BEAN_OVERRIDING;
import static org.alfresco.ampalyser.analyser.runner.CommandOptionsResolver.WHITELIST_BEAN_RESTRICTED_CLASSES;
import static org.alfresco.ampalyser.analyser.runner.CommandOptionsResolver.extractExtensionPath;
import static org.alfresco.ampalyser.analyser.runner.CommandOptionsResolver.extractWarInventoryPaths;
import static org.alfresco.ampalyser.analyser.runner.CommandOptionsResolver.extractWhitelistPath;
import static org.alfresco.ampalyser.analyser.runner.CommandOptionsResolver.validateAnalyserOptions;
import static org.alfresco.ampalyser.analyser.runner.CommandOptionsResolver.validateOptionsForCommand;
import static org.alfresco.ampalyser.analyser.usage.UsagePrinter.printHelp;

import java.util.Iterator;
import java.util.Set;

import org.alfresco.ampalyser.analyser.service.AnalyserService;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

@Service
public class CommandRunner
{
    public static final String VERBOSE = "verbose";
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
            printHelp();
            throw new IllegalArgumentException();
        }
    }

    public void executeExtensionAnalysis(ApplicationArguments args)
    {
        final String extensionPath = extractExtensionPath(args.getNonOptionArgs());
        final Set<String> options = args.getOptionNames();
        
        validateAnalyserOptions(options);
        if (options.isEmpty())
        {
            analyserService.analyse(extensionPath);
            return;
        }

        final String beanOverridingWhitelistPath = extractWhitelistPath(WHITELIST_BEAN_OVERRIDING,
            args);
        final String beanRestrictedClassWhitelistPath = extractWhitelistPath(
            WHITELIST_BEAN_RESTRICTED_CLASSES, args);

        // retrieve provided war inventories, if any
        final Set<String> warInventories = extractWarInventoryPaths(args);
        if (warInventories != null)
        {
            analyserService.analyse(
                extensionPath, 
                null,
                warInventories,
                beanOverridingWhitelistPath, 
                beanRestrictedClassWhitelistPath,
                args.containsOption(VERBOSE));
            return;
        }

        // no war inventories provided
        // check TARGET_VERSION option
        analyserService.analyse(
            extensionPath,
            commandOptionsResolver.extractTargetVersions(args), 
            null,
            beanOverridingWhitelistPath,
            beanRestrictedClassWhitelistPath, 
            args.containsOption(VERBOSE));
    }

    private void listKnownAlfrescoVersions()
    {
        System.out.println("Known Alfresco versions: " + warInventoryReportStore.allKnownVersions());
    }

}
