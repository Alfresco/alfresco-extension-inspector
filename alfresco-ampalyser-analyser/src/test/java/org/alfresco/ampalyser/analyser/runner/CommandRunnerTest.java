/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.runner;

import static org.alfresco.ampalyser.analyser.runner.CommandOptionsResolver.isVerboseOutput;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.alfresco.ampalyser.analyser.service.AnalyserService;
import org.alfresco.ampalyser.analyser.service.ConfigService;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

@ExtendWith(MockitoExtension.class)
public class CommandRunnerTest
{
    @Mock
    private ConfigService configService;
    @Mock
    private WarInventoryReportStore warInventoryReportStore;
    @Mock
    private AnalyserService analyserService;
    @Mock
    private CommandOptionsResolver commandOptionsResolver;
    @InjectMocks
    private CommandRunner commandRunner;

    @BeforeEach
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteEmptyCommand()
    {
        try
        {
            commandRunner.execute(new DefaultApplicationArguments());
            fail("Should have failed.");
        }
        catch (IllegalArgumentException e)
        {
            //Expected
        }
    }

    @Test
    public void testExecuteListKnownVersionsCommand()
    {
        commandRunner.execute(new DefaultApplicationArguments("--list-known-alfresco-versions"));
        verify(warInventoryReportStore).allKnownVersions();

        try
        {
            commandRunner.execute(
                new DefaultApplicationArguments("--list-known-alfresco-versions", "--some-option"));
            fail("`list-known-alfresco-versions` command should not have extra options.");
        }
        catch (IllegalArgumentException e)
        {
            //Expected
        }
    }

    @Test
    public void testExecuteHelpCommand()
    {
        commandRunner.execute(new DefaultApplicationArguments("--help"));

        try
        {
            commandRunner.execute(new DefaultApplicationArguments("help"));
        }
        catch (IllegalArgumentException e)
        {
            //Expected
        }

        try
        {
            commandRunner.execute(
                new DefaultApplicationArguments("--help", "--some-option", "--some-other-option"));
            fail("`help` command should not have extra options.");
        }
        catch (IllegalArgumentException e)
        {
            //Expected
        }
    }

    @Test
    public void testExecuteUnknownCommand()
    {
        try
        {
            commandRunner.execute(new DefaultApplicationArguments("--unknown"));
            fail("`unknown` command should have failed.");
        }
        catch (IllegalArgumentException e)
        {
            //Expected
        }
    }

    @Test
    public void testExecuteExtensionAnalysisNoOptions()
    {
        String extensionFileName = getClass().getClassLoader().getResource("test-extension.amp")
            .getFile();

        commandRunner.execute(new DefaultApplicationArguments(extensionFileName));

        verify(analyserService).analyseAgainstKnownVersions(any());
    }

    @Test
    public void testExecuteExtensionAnalysisMultipleExtensions()
    {
        String extensionFileName = getClass().getClassLoader().getResource("test-extension.amp")
            .getFile();

        assertThrows(IllegalArgumentException.class, () -> commandRunner
            .execute(new DefaultApplicationArguments(extensionFileName, extensionFileName)));
    }

    @Test
    public void testExecuteInvalidExtensionAnalysis()
    {
        String extensionFileName = getClass().getClassLoader().getResource("test-extension.txt")
            .getFile();

        assertThrows(IllegalArgumentException.class,
            () -> commandRunner.execute(new DefaultApplicationArguments(extensionFileName)));

        String anotherExtension = "file-does-not-exist";

        assertThrows(IllegalArgumentException.class,
            () -> commandRunner.execute(new DefaultApplicationArguments(anotherExtension)));
    }

    @Test
    public void testExecuteExtensionAnalysisWithUnknownOptions()
    {
        String extensionFileName = getClass().getClassLoader().getResource("test-extension.amp")
            .getFile();

        assertThrows(IllegalArgumentException.class, () -> commandRunner.execute(
            new DefaultApplicationArguments(extensionFileName, "--unknown-option=its-value")));
    }

    @Test
    public void testExecuteExtensionAnalysisWithOptions()
    {
        String extensionFileName = getClass().getClassLoader().getResource("test-extension.amp")
            .getFile();
        String warInventory = getClass().getClassLoader().getResource("test.inventory.json")
            .getFile();
        String whitelist = getClass().getClassLoader().getResource("test-whitelist.json").getFile();

        commandRunner
            .execute(new DefaultApplicationArguments(extensionFileName, "--target-version=6.2.1"));

        commandRunner.execute(new DefaultApplicationArguments(extensionFileName,
            "--target-inventory=" + warInventory));

        commandRunner.execute(new DefaultApplicationArguments(extensionFileName, "--verbose"));

        verify(analyserService, times(1)).analyseAgainstWarInventories(any());
        verify(analyserService, times(2)).analyseAgainstKnownVersions(any());
    }

    @Test
    public void testExecuteExtensionAnalysisWithAllOptions()
    {
        String extensionFileName = getClass().getClassLoader().getResource("test-extension.amp")
            .getFile();
        String whitelist = getClass().getClassLoader().getResource("test-whitelist.json").getFile();

        commandRunner.execute(
            new DefaultApplicationArguments(extensionFileName, "--target-version=6.2.1",
                "--verbose=false"));

        verify(analyserService).analyseAgainstKnownVersions(any());
    }

    @Test
    public void testExecuteExtensionAnalysisWithBothTargetOptions()
    {
        String extensionFileName = getClass().getClassLoader().getResource("test-extension.amp")
            .getFile();
        String whitelist = getClass().getClassLoader().getResource("test-whitelist.json").getFile();
        String warInventory = getClass().getClassLoader().getResource("test.inventory.json")
            .getFile();

        assertThrows(IllegalArgumentException.class, () -> commandRunner.execute(
            new DefaultApplicationArguments(extensionFileName, "--target-version=6.2.1",
                "--target-inventory=" + warInventory, "--verbose=false")));
    }

    @Test
    public void testExecuteExtensionAnalysisWithInvalidOptionValues()
    {
        String extensionFileName = getClass().getClassLoader().getResource("test-extension.amp")
            .getFile();
        String whitelist = getClass().getClassLoader().getResource("test-whitelist.json").getFile();

        assertThrows(IllegalArgumentException.class, () -> commandRunner.execute(
            new DefaultApplicationArguments(extensionFileName,
                "--target-inventory=" + "war-inventory-not-found.json")));

        // Extension file provided instead of an inventory
        assertThrows(IllegalArgumentException.class, () -> commandRunner.execute(
            new DefaultApplicationArguments(extensionFileName,
                "--target-inventory=" + extensionFileName)));

        assertThrows(IllegalArgumentException.class, () -> commandRunner
            .execute(new DefaultApplicationArguments(extensionFileName, "--verbose=random-value")));
    }

    @Test
    public void testIsVerboseOutput()
    {
        assertThrows(IllegalArgumentException.class,
            () -> isVerboseOutput(new DefaultApplicationArguments("--verbose=no")));
        assertThrows(IllegalArgumentException.class, () -> isVerboseOutput(
            new DefaultApplicationArguments("--verbose=true", "--verbose=false")));

        assertTrue(isVerboseOutput(new DefaultApplicationArguments("--verbose")));
        assertTrue(isVerboseOutput(new DefaultApplicationArguments("--verbose=true")));
        assertFalse(isVerboseOutput(new DefaultApplicationArguments("--verbose=false")));
    }
}
