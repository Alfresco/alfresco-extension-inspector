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

package org.alfresco.extension_inspector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.alfresco.extension_inspector.analyser.runner.AnalyserCommandRunner;
import org.alfresco.extension_inspector.inventory.runner.InventoryCommandRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

@ExtendWith(MockitoExtension.class)
public class ApplicationTests
{
    @Mock
    private InventoryCommandRunner inventoryCommandRunner;
    @Mock
    private AnalyserCommandRunner analyserCommandRunner;
    @InjectMocks
    private Application application;

    @Captor
    ArgumentCaptor<DefaultApplicationArguments> argsCaptor;

    @BeforeEach
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteEmptyCommand()
    {
        application.run(new DefaultApplicationArguments());
        assertEquals(1, application.getExitCode());
    }

    @Test
    public void testExecuteListKnownVersionsCommand()
    {
        String listKnownVersions = "--list-known-alfresco-versions";
        
        application.run(new DefaultApplicationArguments(listKnownVersions));
        assertEquals(0, application.getExitCode());

        application.run(
            new DefaultApplicationArguments(listKnownVersions, "--some-option"));
        assertEquals(1, application.getExitCode());

        application.run(
            new DefaultApplicationArguments(listKnownVersions, "some-option"));
        assertEquals(1, application.getExitCode());
    }

    @Test
    public void testExecuteHelpCommand()
    {
        application.run(new DefaultApplicationArguments("--help"));
        assertEquals(0, application.getExitCode());

        DefaultApplicationArguments arguments = new DefaultApplicationArguments("help");
        application.run(arguments);
        verify(analyserCommandRunner).execute(arguments);

        application
            .run(new DefaultApplicationArguments("--help", "--some-option", "--some-other-option"));
        assertEquals(1, application.getExitCode());
    }

    @Test
    public void testExecuteUnknownCommand()
    {
        application.run(new DefaultApplicationArguments("--unknown"));
        assertEquals(1, application.getExitCode());
    }

    @Test
    public void testExecuteInventoryCommand()
    {
        application.run(new DefaultApplicationArguments("--inventory", "path-to-war-file"));

        verify(inventoryCommandRunner).execute(argsCaptor.capture());
        assertEquals(0, argsCaptor.getValue().getOptionNames().size());
        assertEquals(1, argsCaptor.getValue().getNonOptionArgs().size());
        assertEquals(0, application.getExitCode());
    }

    @Test
    public void testExecuteAnalysingCommand()
    {
        DefaultApplicationArguments args = new DefaultApplicationArguments("path-to-extension");
        
        application.run(args);
        
        verify(analyserCommandRunner).execute(args);
        assertEquals(0, application.getExitCode());
    }
}
