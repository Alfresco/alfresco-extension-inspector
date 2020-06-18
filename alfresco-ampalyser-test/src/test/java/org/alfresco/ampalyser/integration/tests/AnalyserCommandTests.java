package org.alfresco.ampalyser.integration.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.alfresco.ampalyser.AmpalyserClient;
import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.util.AppConfig;
import org.alfresco.ampalyser.util.TestResource;
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
        private AmpalyserClient client;

        private CommandOutput cmdOut;

        @Test
        public void runCommandWithOutput()
        {
                String ampResourcePath = TestResource.getTestResourcePath("ampTest.amp");
                String version = "6.1.1";
                List<String> cmdOptions = List.of(ampResourcePath, "--target=" + version);

                // Generate new analyser report
                cmdOut = client.runAnalyserCommand(cmdOptions);
                System.out.println(cmdOut.getOutput());

                assertEquals(cmdOut.getExitCode(), 0);
        }

        @Test
        public void runCommandWithExitCodeError()
        {
                cmdOut = client.runAnalyserCommand(Collections.emptyList());
                assertEquals(cmdOut.getExitCode(), 1);
                assertTrue(cmdOut.containsMessage("Missing extension file."));
        }
}
