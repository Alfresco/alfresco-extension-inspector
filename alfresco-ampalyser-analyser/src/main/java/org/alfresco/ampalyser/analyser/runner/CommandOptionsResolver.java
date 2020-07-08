/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.runner;

import static org.alfresco.ampalyser.analyser.usage.UsagePrinter.printAnalyserUsage;
import static org.alfresco.ampalyser.analyser.usage.UsagePrinter.printCommandUsage;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.alfresco.ampalyser.analyser.store.AlfrescoTargetVersionParser;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class CommandOptionsResolver
{
    private static final Set<String> ANALYSER_COMMAND_OPTIONS = Set
        .of("target", "whitelistBeanOverriding", "whitelistBeanRestrictedClasses", "verbose");

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

    public static String extractWhitelistPath(String whitelistOption, ApplicationArguments args)
    {
        final List<String> whitelistPaths = args.getOptionValues(whitelistOption);
        if (whitelistPaths == null)
        {
            return null;
        }
        if (whitelistPaths.isEmpty())
        {
            printAnalyserUsage("Invalid whitelist path provided (missing value).");
            throw new IllegalArgumentException();
        }
        if (whitelistPaths.size() > 1)
        {
            printAnalyserUsage(
                "Multiple whitelists provided.(command option '" + whitelistOption + "')");
            throw new IllegalArgumentException();
        }
        String path = whitelistPaths.get(0);
        if (!new File(path).exists() || !FilenameUtils.getExtension(path).equalsIgnoreCase("json"))
        {
            printAnalyserUsage("The whitelist file is not valid or does not exist.(command option '"
                + whitelistOption + "') Supported file format is JSON.");
            throw new IllegalArgumentException();
        }
        return path;
    }

    public SortedSet<String> extractTargetVersions(ApplicationArguments args)
    {
        final SortedSet<String> versions = alfrescoTargetVersionParser
            .parse(args.getOptionValues("target"));
        if (versions.isEmpty())
        {
            printAnalyserUsage("Target ACS version was not recognised.");
            throw new IllegalArgumentException();
        }
        return versions;
    }

    public static void validateAnalyserOptions(Set<String> options)
    {
        if (!ANALYSER_COMMAND_OPTIONS.containsAll(options))
        {
            printAnalyserUsage("Unknown options provided.");
            throw new IllegalArgumentException();
        }
    }

    public static void validateOptionsForCommand(String command, Iterator<String> commandOptions)
    {
        if (commandOptions.hasNext())
        {
            printCommandUsage(command, "Unknown options provided for '" + command + "' command.");
            throw new IllegalArgumentException();
        }
    }

    private static boolean isExtensionValid(final String extensionPath)
    {
        return new File(extensionPath).exists() && (
            FilenameUtils.getExtension(extensionPath).equalsIgnoreCase("amp") || FilenameUtils
                .getExtension(extensionPath).equalsIgnoreCase("jar"));
    }
}
