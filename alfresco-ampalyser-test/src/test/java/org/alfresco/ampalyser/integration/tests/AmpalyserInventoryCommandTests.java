package org.alfresco.ampalyser.integration.tests;

import org.alfresco.ampalyser.util.TestResource;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AmpalyserInventoryCommandTests extends AmpalyserInventoryTests
{
        @Test
        public void runCommandWithOutput()
        {
                String warResourcePath = TestResource.getTestResourcePath("inventoryTest.war");
                String inventoryReportPath = TestResource.getTargetPath();
                List<String> cmdOptions = new ArrayList<>()
                {{
                        add(warResourcePath);
                        add("--o=" + inventoryReportPath);
                }};

                // Generate new inventory report
                cmdOut = client.runInventoryAnalyserCommand(cmdOptions);
                File inventoryReport = new File(inventoryReportPath + File.separator + TestResource.getTestInventoryReport().getName());

                Assert.assertEquals(cmdOut.getExitCode(), 0);
                Assert.assertTrue(cmdOut.containsMessage(SUCCESS_MESSAGE), "Inventory report has not been generated");
                Assert.assertTrue(inventoryReport.exists());
        }
}
