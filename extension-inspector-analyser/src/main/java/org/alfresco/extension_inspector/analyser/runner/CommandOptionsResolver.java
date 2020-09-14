/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.extension_inspector.analyser.runner;

import static java.util.stream.Collectors.toSet;
import static org.alfresco.extension_inspector.analyser.usage.UsagePrinter.printAnalyserUsage;
import static org.alfresco.extension_inspector.analyser.usage.UsagePrinter.printCommandUsage;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import org.alfresco.extension_inspector.analyser.store.AlfrescoTargetVersionParser;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class CommandOptionsResolver
{
    public static final String TARGET_VERSION = "target-version";
    public static final String TARGET_INVENTORY = "target-inventory";
    public static final String VERBOSE = "verbose";
    public static final String HELP = "help";
    public static final String LIST_KNOWN_VERSIONS = "list-known-alfresco-versions";
    
    @Autowired
    private AlfrescoTargetVersionParser alfrescoTargetVersionParser;

    public static String extractExtensionPath(List<String> nonOptionArgs)
    {
        if (nonOptionArgs.size() > 1)
        {
            printAnalyserUsage("Multiple extension files have been provided.");
            throw new IllegalArgumentException();
        }

        String extensionPath = nonOptionArgs.get(0);
        if (!isExtensionValid(extensionPath))
        {
            printAnalyserUsage(
                "The extension file is not valid or does not exist. Supported file formats are AMP and JAR.");
            throw new IllegalArgumentException();
        }

        return extensionPath;
    }
    
    public SortedSet<String> extractTargetVersions(ApplicationArguments args)
    {
        final SortedSet<String> versions = alfrescoTargetVersionParser
            .parse(args.getOptionValues(TARGET_VERSION));
        if (versions.isEmpty())
        {
            printAnalyserUsage("Target ACS version was not recognised.");
            throw new IllegalArgumentException();
        }
        return versions;
    }

    public static Set<String> extractWarInventoryPaths(ApplicationArguments args)
    {
        List<String> values = args.getOptionValues(TARGET_INVENTORY);
        // TARGET_INVENTORY option was not provided, will check TARGET_VERSION
        if (values == null)
        {
            return null;
        }
        // TARGET_INVENTORY option was provided but without a value, thus throw exception
        if(values.isEmpty())
        {
            printAnalyserUsage("Invalid target options (missing values)!");
            throw new IllegalArgumentException();
        }
        
        Set<String> inventories = values
            .stream()
            .filter(CommandOptionsResolver::isInventoryValid)
            .collect(toSet());

        if (inventories.isEmpty())
        {
            printAnalyserUsage("Target war inventories are not valid.");
            throw new IllegalArgumentException();
        }
        return inventories;
    }
    
    public static void validateAnalyserOptions(Set<String> options)
    {
        if (options == null || options.isEmpty())
        {
            // Analysis will be done with default options
            return;
        }

        Set<String> knownCommandOptions = Set.of(TARGET_VERSION, TARGET_INVENTORY, VERBOSE);
        if (!knownCommandOptions.containsAll(options))
        {
            printAnalyserUsage("Unknown options provided.");
            throw new IllegalArgumentException();
        }
        
        if(options.containsAll(Set.of(TARGET_VERSION, TARGET_INVENTORY)))
        {
            printAnalyserUsage("Both target options have been provided.");
            throw new IllegalArgumentException();
        }
    }

    public static void validateOptionsForCommand(String command, Iterator<String> commandOptions)
    {
        if (Set.of(HELP, LIST_KNOWN_VERSIONS).contains(command) && commandOptions != null
            && commandOptions.hasNext())
        {
            printCommandUsage("--" + command,
                "Unknown options provided for '" + command + "' command.");
            throw new IllegalArgumentException();
        }
    }

    private static boolean isExtensionValid(final String extensionPath)
    {
        return new File(extensionPath).exists() && (
            FilenameUtils.getExtension(extensionPath).equalsIgnoreCase("amp") || FilenameUtils
                .getExtension(extensionPath).equalsIgnoreCase("jar"));
    }

    private static boolean isInventoryValid(final String warInventory)
    {
        return new File(warInventory).exists() && FilenameUtils.getExtension(warInventory)
            .equalsIgnoreCase("json");
    }

    public static boolean isVerboseOutput(ApplicationArguments args)
    {
        if (!args.containsOption(VERBOSE))
        {
            return false;
        }
        List<String> values = args.getOptionValues(VERBOSE);
        if (values.isEmpty())
        {
            return true;
        }
        if (values.size() > 1 ||
            (!values.get(0).equalsIgnoreCase("true") &&
             !values.get(0).equalsIgnoreCase("false")))
        {
            printAnalyserUsage("Invalid values for verbose option provided.");
            throw new IllegalArgumentException();
        }
        return Boolean.parseBoolean(values.get(0));
    }
}
