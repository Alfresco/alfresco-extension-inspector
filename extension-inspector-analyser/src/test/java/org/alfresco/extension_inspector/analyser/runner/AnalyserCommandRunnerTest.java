/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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

package org.alfresco.extension_inspector.analyser.runner;

import static org.alfresco.extension_inspector.analyser.runner.CommandOptionsResolver.isVerboseOutput;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    public void testListAllKnownVersions()
    {
        commandRunner.listKnownAlfrescoVersions();
        verify(warInventoryReportStore).allKnownVersions();
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
