/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.extension_inspector;

import static java.text.MessageFormat.format;
import static java.util.Arrays.copyOfRange;
import static org.alfresco.extension_inspector.usage.UsagePrinter.printCommandUsage;
import static org.alfresco.extension_inspector.usage.UsagePrinter.printHelp;
import static org.alfresco.extension_inspector.usage.UsagePrinter.printInventoryUsage;

import org.alfresco.extension_inspector.analyser.runner.AnalyserCommandRunner;
import org.alfresco.extension_inspector.inventory.runner.InventoryCommandRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@SpringBootApplication
public class Application implements ApplicationRunner, ExitCodeGenerator
{
    private static final String INVENTORY_ARG = "inventory";
    private static final String HELP_ARG = "help";
    private static final String LIST_KNOWN_VERSIONS_ARG = "list-known-alfresco-versions";

    @Autowired
    private InventoryCommandRunner inventoryCommandRunner;
    @Autowired
    private AnalyserCommandRunner analyserCommandRunner;

    public static void main(String[] args)
    {
        System.exit(SpringApplication.exit(SpringApplication.run(Application.class, args)));
    }

    @Override
    public void run(final ApplicationArguments args)
    {
        try
        {
            switch (retrieveMainDirectiveArg(args))
            {
            case HELP_ARG:
                printHelp();
                break;
            case LIST_KNOWN_VERSIONS_ARG:
                analyserCommandRunner.listKnownAlfrescoVersions();
                break;
            case INVENTORY_ARG:
                inventoryCommandRunner.execute(stripFirstArgument(args));
                break;
            default:
                analyserCommandRunner.execute(args);
            }
        }
        catch (IllegalArgumentException e)
        {
            setExceptionExitCode();
        }
    }

    private static String retrieveMainDirectiveArg(final ApplicationArguments args)
    {
        if (args.getOptionNames().isEmpty())
        {
            if (args.getNonOptionArgs().isEmpty())
            {
                System.out.println("error: missing arguments");
                printHelp();
                throw new IllegalArgumentException();
            }

            return "analyse"; // not an actual command line option, just the default behaviour
        }

        if (args.getOptionNames().contains(HELP_ARG))
        {
            if (!args.getNonOptionArgs().isEmpty() || args.getOptionNames().size() > 1)
            {
                printCommandUsage("--" + HELP_ARG,
                    "Unknown options provided for '" + HELP_ARG + "' command.");
                throw new IllegalArgumentException();
            }

            return HELP_ARG;
        }

        if (args.getOptionNames().contains(LIST_KNOWN_VERSIONS_ARG))
        {
            if (!args.getNonOptionArgs().isEmpty() || args.getOptionNames().size() > 1)
            {
                printCommandUsage("--" + LIST_KNOWN_VERSIONS_ARG,
                    "Unknown options provided for '" + LIST_KNOWN_VERSIONS_ARG + "' command.");
                throw new IllegalArgumentException();
            }

            return LIST_KNOWN_VERSIONS_ARG;
        }
        
        if (args.getOptionNames().contains(INVENTORY_ARG))
        {
            // if "--inventory" is specified, but it's not the first argument
            if (!format("--{0}", INVENTORY_ARG).equals(args.getSourceArgs()[0]))
            {
                printInventoryUsage(
                    "Invalid argument order: --" + INVENTORY_ARG + " should be the first argument");
                throw new IllegalArgumentException();
            }

            if (!args.getOptionValues(INVENTORY_ARG).isEmpty())
            {
                printInventoryUsage("Invalid argument format for: --" + INVENTORY_ARG);
                throw new IllegalArgumentException();
            }

            return INVENTORY_ARG;
        }

        // some options were provided, but no extension file path
        if (args.getNonOptionArgs().isEmpty())
        {
            // if we reached this point, the arguments were not recognised
            System.out.println("error: unknown arguments provided");
            printHelp();
            throw new IllegalArgumentException();
        }

        return "analyse"; // not an actual command line option, just the default behaviour
    }

    private static DefaultApplicationArguments stripFirstArgument(final ApplicationArguments args)
    {
        final String[] rawArgs = args.getSourceArgs();
        return new DefaultApplicationArguments(copyOfRange(rawArgs, 1, rawArgs.length));
    }

    //region Beans
    @Bean
    public ObjectMapper objectMapper()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }
    //endregion

    //region Exception Handling
    private static final int EXIT_CODE_EXCEPTION = 1;
    private int exitCode = 0;

    @Bean
    ExitCodeExceptionMapper exitCodeToExceptionMapper()
    {
        return exception -> {
            // Set specific exit codes based on the exception type

            // Default exit code
            return EXIT_CODE_EXCEPTION;
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
    //endregion
}
