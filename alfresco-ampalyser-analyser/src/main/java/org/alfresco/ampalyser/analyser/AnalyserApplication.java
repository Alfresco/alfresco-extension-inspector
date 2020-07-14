/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.alfresco.ampalyser.analyser.runner.CommandRunner;
import org.alfresco.ampalyser.inventory.AlfrescoWarInventory;
import org.alfresco.ampalyser.inventory.InventoryApplication;
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

@SpringBootApplication
@ComponentScan(
    basePackages = {"org.alfresco.ampalyser.inventory", "org.alfresco.ampalyser.analyser"},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {AlfrescoWarInventory.class, InventoryApplication.class })
    })
public class AnalyserApplication implements ApplicationRunner, ExitCodeGenerator
{
    private static final int EXIT_CODE_EXCEPTION = 1;

    @Autowired
    private CommandRunner commandRunner;

    private int exitCode = 0;

    public static void main(String[] args)
    {
        System.exit(SpringApplication.exit(SpringApplication.run(AnalyserApplication.class, args)));
    }

    @Override
    public void run(ApplicationArguments args)
    {
        try
        {
            commandRunner.execute(args);
        }
        catch (IllegalArgumentException e)
        {
            setExceptionExitCode();
        }
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
