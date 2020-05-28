package org.alfresco.ampalyser.integration.tests;

import org.alfresco.ampalyser.AmpalyserClient;
import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.models.Resource;
import org.alfresco.ampalyser.util.TestResource;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AmpalyserInventoryTests
{
        private static final String SUCCESS_MESSAGE = "Inventory report generated";
        AmpalyserClient client = new AmpalyserClient();

        File inventoryReport;

        @BeforeTest
        public void executeCommand()
        {
                // Delete inventory report if exists
                inventoryReport = TestResource.getTestInventoryReport();
                if (inventoryReport.exists())
                {
                        inventoryReport.delete();
                }

                String warResourcePath = TestResource.getTestResourcePath("inventoryTest.war");
                List<String> cmdOptions = new ArrayList<>()
                {{
                        add(warResourcePath);
                }};

                // Generate new inventory report
                CommandOutput cmdOut = client.runInventoryAnalyserCommand(cmdOptions);
                Assert.assertEquals(cmdOut.getExitCode(), 0);
                Assert.assertTrue(cmdOut.containsMessage(SUCCESS_MESSAGE), "Inventory report has not been generated");

                // Get the new inventory report
                inventoryReport = TestResource.getTestInventoryReport();
        }

        @Test
        public void jsonReportExists()
        {
                Assert.assertEquals(inventoryReport.exists(), true);
        }

        @Test
        public void readJson()
        {
                List<Resource> report = client.getInventoryResources("ALFRESCO_PUBLIC_API", inventoryReport);
                Assert.assertEquals(report.size(), 0);
        }

        @Test
        public void checkBeanType()
        {
                List<Resource> report = client.getInventoryResources("BEAN", inventoryReport);
                Assert.assertEquals(report.size(), 2);
        }

        @Test
        public void checkClassPathType()
        {
                List<Resource> report = client.getInventoryResources("CLASSPATH_ELEMENT", inventoryReport);
                Assert.assertEquals(report.size(), 399);
        }

        @Test
        public void checkFileType()
        {
                List<Resource> report = client.getInventoryResources("FILE", inventoryReport);
                Assert.assertEquals(report.size(), 7);

                Resource resource = client.getInventoryResource("FILE", "META-INF/MANIFEST.MF", inventoryReport);
                Assert.assertEquals(resource.getDefiningObject().equals("META-INF/MANIFEST.MF"), true, "");
                Assert.assertEquals(resource.getId().equals("META-INF/MANIFEST.MF"), true, "");
        }

}
