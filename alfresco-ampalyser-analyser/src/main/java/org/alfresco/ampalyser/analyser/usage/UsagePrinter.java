/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.usage;

import static java.lang.String.join;

public class UsagePrinter
{
    private static final String EXTENSION_FILENAME = "<extension-filename>";
    private static final String TARGET_OPTION = "[--target-version=6.1.0[-7.0.0] | --target-inventory=/path/to/war_inventory.json]";
    private static final String WHITELIST_BEAN_OVERRIDING_PATH = "[--whitelistBeanOverriding=/path/to/bean_overriding_whitelist.json]";
    private static final String WHITELIST_BEAN_RESTRICTED_CLASSES_PATH = "[--whitelistBeanRestrictedClasses=/path/to/bean_restricted_classes_whitelist.json]";
    private static final String VERBOSE = "[--verbose]";
    private static final String HELP = "[--help]";
    private static final String LIST_KNOWN_VERSIONS = "[--list-known-alfresco-versions]";

    public static void printHelp()
    {
        printUsage(
            HELP, 
            LIST_KNOWN_VERSIONS,
            join(" ",
                EXTENSION_FILENAME,
                TARGET_OPTION,
                WHITELIST_BEAN_OVERRIDING_PATH,
                WHITELIST_BEAN_RESTRICTED_CLASSES_PATH,
                VERBOSE));
        //TODO
    }

    public static void printAnalyserUsage(String errorMessage)
    {
        System.out.println("error: " + errorMessage);
        printUsage(join(" ", 
            EXTENSION_FILENAME, 
            TARGET_OPTION, 
            WHITELIST_BEAN_OVERRIDING_PATH,
            WHITELIST_BEAN_RESTRICTED_CLASSES_PATH, 
            VERBOSE));
    }
    
    public static void printCommandUsage(String command, String errorMessage)
    {
        System.out.println("error: " + errorMessage);
        printUsage(command);
    }

    public static void printUsage(String... supportedCommands)
    {
        System.out.println("Usage: ");
        System.out.println(
            "java -jar alfresco-ampalyser-analyser.jar " + join("\n   ", supportedCommands));
    }
}
