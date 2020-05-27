package org.alfresco.ampalyser.integration.tests;

import java.io.File;

import org.alfresco.ampalyser.AmpalyserClient;
import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.models.Resource;
import org.alfresco.ampalyser.util.JsonInventoryParser;
import org.alfresco.ampalyser.util.TestResource;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;

public class AmpalyserInventoryTests
{
        public String filePath = "./inventoryTest.inventory.json";
        AmpalyserClient client = new AmpalyserClient();
        JsonInventoryParser json = new JsonInventoryParser();

        @BeforeTest
        public void executeCommand() {
                File jsonFile = new File(filePath);
                if (jsonFile.exists()) {
                        jsonFile.delete();
                }
                String warResourcePath = TestResource.getTestResourcePath("inventoryTest.war");
                List<String> cmdOptions = new ArrayList<>(){{add(warResourcePath);}};

                CommandOutput cmdOut = client.runInventoryAnalyserCommand(cmdOptions);
                Assert.assertEquals(cmdOut.getExitCode(), 0);

        }

        @Test
        public void jsonReportExists()
        {
                File jsonFile = new File(filePath);
                Assert.assertEquals(jsonFile.exists(), true);
        }

        @Test
        public void readJson()
        {
                List<Resource> report = json.getResources( "ALFRESCO_PUBLIC_API", new File(filePath));
                Assert.assertEquals(report.size(), 0 );
        }

        @Test
        public void checkBeanType()
        {
                List<Resource> report = json.getResources( "BEAN", new File(filePath));
                Assert.assertEquals(report.size(), 2 );
        }

        @Test
        public void checkClassPathType()
        {
                List<Resource> report = json.getResources( "CLASSPATH_ELEMENT", new File(filePath));
                Assert.assertEquals(report.size(), 399 );
        }


        @Test
        public void checkFileType()
        {
                File jsonFile = new File(filePath);
                List<Resource> report = json.getResources( "FILE", jsonFile);
                Assert.assertEquals(report.size(), 7 );

                Resource resource1 = JsonInventoryParser.getResource( "FILE","META-INF/MANIFEST.MF", jsonFile);
                Assert.assertEquals(resource1.definingObject.equals("META-INF/MANIFEST.MF"), true, "");
                Assert.assertEquals(resource1.id.equals("META-INF/MANIFEST.MF"), true, "");
                Assert.assertEquals(resource1.definingObject.equals("META-INF/MANIFEST.MF"), true, "");

        }


}
