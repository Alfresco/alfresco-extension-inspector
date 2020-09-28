/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.extension_inspector.analyser.usage;

import static java.lang.String.join;

import java.util.Arrays;

public class UsagePrinter
{
    private static final String EXTENSION_FILENAME = "<extension-filename>";
    private static final String TARGET_VERSION = "--target-version";
    private static final String TARGET_INVENTORY = "--target-inventory";
    private static final String TARGET_OPTION =
        "[" + TARGET_VERSION + "=6.1.0[-7.0.0] | " 
            + TARGET_INVENTORY + "=/path/to/war_inventory.json]";
    private static final String VERBOSE = "--verbose";
    private static final String VERBOSE_OPTION = "[" + VERBOSE + "=[true | false]]";
    private static final String HELP = "--help";
    private static final String LIST_KNOWN_VERSIONS = "--list-known-alfresco-versions";

    private static final String format = "   %-36s %s";

    public static void printHelp()
    {
        System.out.println("Extension analyser:");
        printUsage(
            join(" ",
                EXTENSION_FILENAME,
                TARGET_OPTION,
                VERBOSE_OPTION),
            HELP, 
            LIST_KNOWN_VERSIONS);
        
        System.out.println("Options:");

        System.out.printf(format, TARGET_VERSION,
            "An Alfresco version or a range of Alfresco versions.\n");
        System.out.printf(format, TARGET_INVENTORY,
            "A file path of an existing WAR inventory.\n");
        System.out.printf(format, VERBOSE, "Verbose output.\n");
        System.out.printf(format, HELP, "Shows this screen.\n");
        System.out.printf(format, LIST_KNOWN_VERSIONS,
            "Lists all Alfresco versions with inventory reports included in the tool.");
    }

    public static void printAnalyserUsage(String errorMessage)
    {
        System.out.println("error: " + errorMessage);
        printUsage(join(" ", 
            EXTENSION_FILENAME, 
            TARGET_OPTION,
            VERBOSE_OPTION));
    }
    
    public static void printCommandUsage(String command, String errorMessage)
    {
        System.out.println("error: " + errorMessage);
        printUsage(command);
    }

    public static void printUsage(String... supportedCommands)
    {
        System.out.println("Usage: ");
        Arrays
            .stream(supportedCommands)
            .forEach(command -> 
                System.out.println("   java -jar alfresco-extension-inspector-analyser.jar " + command));
    }
}
