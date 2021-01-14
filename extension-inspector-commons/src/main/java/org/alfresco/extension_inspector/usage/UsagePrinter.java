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

package org.alfresco.extension_inspector.usage;

import static java.lang.String.join;

import java.util.Arrays;

public class UsagePrinter
{
    private static final String EXTENSION_FILENAME = "<extension-filename>";
    private static final String WAR_FILENAME = "<alfresco-war-filename>";
    private static final String TARGET_VERSION = "--target-version";
    private static final String TARGET_INVENTORY = "--target-inventory";
    private static final String TARGET_OPTION =
        "[" + TARGET_VERSION + "=6.1.0[-7.0.0] | " 
            + TARGET_INVENTORY + "=<report_file_path>.json]";
    private static final String VERBOSE = "--verbose";
    private static final String VERBOSE_OPTION = "[" + VERBOSE + "=[true | false]]";
    private static final String HELP = "--help";
    private static final String LIST_KNOWN_VERSIONS = "--list-known-alfresco-versions";
    private static final String INVENTORY = "--inventory";
    private static final String INVENTORY_OUTPUT = "[--o=<report_file_path>.json]";

    private static final String format = "   %-36s %s";

    public static void printHelp()
    {
        printUsage(
            join(" ",
                EXTENSION_FILENAME,
                TARGET_OPTION,
                VERBOSE_OPTION),
            join(" ",
                INVENTORY,
                WAR_FILENAME,
                INVENTORY_OUTPUT),
            HELP, 
            LIST_KNOWN_VERSIONS);
        
        System.out.println("Options:");

        System.out.printf(format, TARGET_VERSION,
            "An Alfresco version or a range of Alfresco versions.\n");
        System.out.printf(format, TARGET_INVENTORY,
            "A file path of an existing WAR inventory.\n");
        System.out.printf(format, VERBOSE, "Verbose output.\n");
        System.out.printf(format, INVENTORY,
            "Creates an inventory report in json format for the specified war or extension file.\n");
        System.out.printf(format, INVENTORY_OUTPUT, "A file path for the new inventory report.\n");
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

    public static void printInventoryUsage(String errorMessage)
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
                System.out.println("   java -jar alfresco-extension-inspector.jar " + command));
    }
}
