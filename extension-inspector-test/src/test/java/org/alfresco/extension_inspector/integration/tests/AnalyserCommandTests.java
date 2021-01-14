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
package org.alfresco.extension_inspector.integration.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.extension_inspector.ExtensionInspectorClient;
import org.alfresco.extension_inspector.models.CommandOutput;
import org.alfresco.extension_inspector.util.AppConfig;
import org.alfresco.extension_inspector.util.TestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(classes = AppConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AnalyserCommandTests extends AbstractTestNGSpringContextTests
{
        @Autowired
        private ExtensionInspectorClient client;

        private CommandOutput cmdOut;

        @Test
        public void runCommandWithOutput()
        {
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest.amp");
                String version = "6.1.1";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version, "--verbose");

                // Generate new analyser report
                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions);
                System.out.println(cmdOut.getOutput());
                assertEquals(cmdOut.getExitCode(), 0);
        }

        @Test
        public void runCommandWithExitCodeError()
        {
                String ampResourcePath = TestResource.getTestResourcePath("test.txt");
                List<String> cmdOptions = List.of(ampResourcePath);
                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions);
                assertEquals(cmdOut.getExitCode(), 1);
                assertTrue(cmdOut.isInOutput("The extension file is not valid or does not exist. Supported file formats are AMP and JAR."));
        }

        @Test
        public void testAnalyseAmpOldVersion()
        {
                // Run against old uncached Alfresco version
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest.amp");
                String version = "4.0.0";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions);

                assertTrue(cmdOut.isInOutput("Target ACS version was not recognised"));
        }
}
