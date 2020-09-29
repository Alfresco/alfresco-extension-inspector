/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.extension_inspector.analyser.runner;

import static org.alfresco.extension_inspector.analyser.runner.CommandOptionsResolver.isVerboseOutput;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.alfresco.extension_inspector.analyser.service.AnalyserService;
import org.alfresco.extension_inspector.analyser.service.ConfigService;
import org.alfresco.extension_inspector.analyser.store.WarInventoryReportStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

@ExtendWith(MockitoExtension.class)
public class AnalyserCommandRunnerTest
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
    private AnalyserCommandRunner commandRunner;

    @BeforeEach
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
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
