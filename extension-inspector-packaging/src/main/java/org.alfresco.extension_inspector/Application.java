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

import org.alfresco.extension_inspector.analyser.runner.AnalyserCommandRunner;
import org.alfresco.extension_inspector.analyser.usage.UsagePrinter;
import org.alfresco.extension_inspector.inventory.runner.InventoryCommandRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static final String INVENTORY_ARG = "inventory";
    private static final String HELP_ARG = "help";

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
                printUsage();
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
            logger.error("Missing arguments");
            printUsage();
            throw new IllegalArgumentException();
        }

        if (args.getOptionNames().contains(HELP_ARG))
        {
            return HELP_ARG;
        }

        if (args.getOptionNames().contains(INVENTORY_ARG))
        {
            // if "--inventory" is specified, but it's not the first argument
            if (!format("--{0}", INVENTORY_ARG).equals(args.getSourceArgs()[0]))
            {
                logger.error("Invalid argument order: \"--{}\" should be the first argument",
                    INVENTORY_ARG);
                printUsage();
                throw new IllegalArgumentException();
            }

            if (!args.getOptionValues(INVENTORY_ARG).isEmpty())
            {
                logger.error("Invalid argument format for: --{}", INVENTORY_ARG);
                printUsage();
                throw new IllegalArgumentException();
            }

            return INVENTORY_ARG;
        }

        return "analyse"; // not an actual command line option, just the default behaviour
    }

    private static void printUsage()
    {
        System.out.println("Usage:");
        UsagePrinter.printHelp();
        System.out.println();
        InventoryCommandRunner.printUsage();
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
