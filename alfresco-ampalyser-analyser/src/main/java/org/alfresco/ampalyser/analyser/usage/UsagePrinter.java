/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.usage;

import org.apache.commons.lang3.StringUtils;

public class UsagePrinter
{
    public static void printHelp()
    {
        printUsage("[--help]", "[--list-known-alfresco-versions]",
            "<extension-filename> [--target=6.1.0[-7.0.0]] "
                + "[--whitelistBeanOverriding=/path/to/bean_overriding_whitelist.json] "
                + "[--whitelistBeanRestrictedClasses=/path/to/bean_restricted_classes_whitelist.json] "
                + "[--verbose]");
        //TODO write manual
    }

    public static void printAnalyserUsage()
    {
        printUsage("<extension-filename> [--target=6.1.0[-7.0.0]] "
            + "[--whitelistBeanOverriding=/path/to/bean_overriding_whitelist.json] "
            + "[--whitelistBeanRestrictedClasses=/path/to/bean_restricted_classes_whitelist.json] "
            + "[--verbose]");
    }

    public static void printUsage(String... supportedCommands)
    {
        System.out.println("Usage: ");
        System.out.println("java -jar alfresco-ampalyser-analyser.jar " + StringUtils
            .joinWith("\n", supportedCommands));
    }
}
