package org.alfresco.ampalyser.integration.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.alfresco.ampalyser.AmpalyserClient;
import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.util.AppConfig;
import org.alfresco.ampalyser.util.TestResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AnalyserCommandTests
{
        @Autowired
        private AmpalyserClient client;

        private CommandOutput cmdOut;

        @Test
        public void runCommandWithOutput()
        {
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest.amp");
                String version = "6.1.1";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version, "--verbose");

                // Generate new analyser report
                cmdOut = client.runCommand(cmdOptions);
                System.out.println(cmdOut.getOutput());
                assertEquals(cmdOut.getExitCode(), 0);
        }

        @Test
        public void runCommandWithExitCodeError()
        {
                String ampResourcePath = TestResource.getTestResourcePath("test.txt");
                List<String> cmdOptions = List.of(ampResourcePath);
                cmdOut = client.runCommand(cmdOptions);
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

                cmdOut = client.runCommand(cmdOptions);

                assertTrue(cmdOut.isInOutput("Target ACS version was not recognised"));
        }
}
