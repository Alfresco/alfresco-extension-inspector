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
