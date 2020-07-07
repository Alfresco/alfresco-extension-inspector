/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser;

import static org.alfresco.ampalyser.analyser.CommandOptionsResolver.checkCommandArgs;
import static org.alfresco.ampalyser.analyser.CommandOptionsResolver.extractExtensionPath;
import static org.alfresco.ampalyser.analyser.CommandOptionsResolver.extractWhitelistPath;
import static org.alfresco.ampalyser.analyser.CommandOptionsResolver.validateAnalyserOptions;
import static org.alfresco.ampalyser.analyser.usage.UsagePrinter.printHelp;
import static org.alfresco.ampalyser.analyser.usage.UsagePrinter.printUsage;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.alfresco.ampalyser.analyser.service.AnalyserService;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.alfresco.ampalyser.inventory.AlfrescoWarInventory;
import org.alfresco.ampalyser.inventory.InventoryApplication;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@SpringBootApplication
@ComponentScan(
    basePackages = {"org.alfresco.ampalyser.inventory", "org.alfresco.ampalyser.analyser"},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {AlfrescoWarInventory.class, InventoryApplication.class })
    })
public class AnalyserApplication implements ApplicationRunner, ExitCodeGenerator
{
    private static final Logger logger = LoggerFactory.getLogger(AnalyserApplication.class);

    private static final int EXIT_CODE_EXCEPTION = 1;

    @Autowired
    private CommandOptionsResolver commandOptionsResolver;

    @Autowired
    private WarInventoryReportStore warInventoryReportStore;
    
    @Autowired
    private AnalyserService analyserService;

    private int exitCode = 0;

    public static void main(String[] args)
    {
        System.exit(SpringApplication.exit(SpringApplication.run(AnalyserApplication.class, args)));
    }

    @Override
    public void run(ApplicationArguments args)
    {
        final Set<String> options = args.getOptionNames();
        final List<String> nonOptionArgs = args.getNonOptionArgs();

        try
        {
            // Stop if no command arguments have been provided
            checkCommandArgs(options, nonOptionArgs);
            
            if (nonOptionArgs.isEmpty())
            {
                Iterator<String> iterator = options.iterator();
                execute(iterator.next(), iterator);
            }
            else
            {
                final String extensionPath = extractExtensionPath(nonOptionArgs);
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
        }
        catch (IllegalArgumentException e)
        {
            logger.error(e.getMessage());
            setExceptionExitCode();
            return;
        }
    }

    public void execute(String command, Iterator<String> commandOptions)
    {
        switch (command)
        {
        case "help":
            if (commandOptions.hasNext())
            {
                printUsage("--help");
                throw new IllegalArgumentException(
                    "Unknown options provided for '--help' command.");
            }
            printHelp();
            break;
        case "list-known-alfresco-versions":
            if (commandOptions.hasNext())
            {
                printUsage("--list-known-alfresco-versions");
                throw new IllegalArgumentException(
                    "Unknown options provided for '--list-known-alfresco-versions' command.");
            }
            listKnownAlfrescoVersions();
            break;
        default:
            printUsage("[--help]", "[--list-known-alfresco-versions]",
                "<extension-filename> [--target=6.1.0[-7.0.0]] "
                    + "[--whitelistBeanOverriding=/path/to/bean_overriding_whitelist.json] "
                    + "[--whitelistBeanRestrictedClasses=/path/to/bean_restricted_classes_whitelist.json] "
                    + "[--verbose]");
            throw new IllegalArgumentException("Unknown command provided: " + command);
        }
    }

    private void listKnownAlfrescoVersions()
    {
        System.out.println("Known Alfresco versions: " + StringUtils
            .joinWith(", ", warInventoryReportStore.allKnownVersions()));
    }

    @Bean
    public ObjectMapper objectMapper()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }

    @Bean
    ExitCodeExceptionMapper exitCodeToExceptionMapper()
    {
        return exception -> {
            // Set specific exit codes based on the exception type

            // Default exit code
            return 1;
        };
    }

    /**
     * @return the code 1 if an exception occurs. Otherwise, on a clean exit, it
     * provides 0 as the exit code.
     */
    @Override
    public int getExitCode()
    {
        return exitCode;
    }

    /**
     * Set the exit code to default exception exit code.
	 */
	private void setExceptionExitCode()
	{
		this.exitCode = EXIT_CODE_EXCEPTION;
	}
}
