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
        private static final String PUBLIC_API_TYPE = "ALFRESCO_PUBLIC_API";
        private static final String BEAN_TYPE = "BEAN";
        private static final String CLASSPATH_ELEMENT_TYPE = "CLASSPATH_ELEMENT";
        private static final String FILE_TYPE = "FILE";

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
        public void testJsonReportExists()
        {
                Assert.assertEquals(inventoryReport.exists(), true);
        }

        @Test
        public void testPublicApiAnnotation()
        {
                List<Resource> publicApiRs = client.getInventoryResources(PUBLIC_API_TYPE, inventoryReport);
                Assert.assertEquals(publicApiRs.size(), 2);

                Resource publicApiRs1 = client.getInventoryResource(PUBLIC_API_TYPE, "org.alfresco.repo.node.NodeServicePolicies", inventoryReport);
                Assert.assertNotNull(publicApiRs1);
                Assert.assertFalse(publicApiRs1.getDeprecated());

                Resource publicApiRs2 = client.getInventoryResource(PUBLIC_API_TYPE, "org.alfresco.repo.content.transform.TransformerConfig", inventoryReport);
                Assert.assertNotNull(publicApiRs2);
                Assert.assertTrue(publicApiRs2.getDeprecated());
        }

        @Test
        public void checkBeanTypeSize()
        {
                List<Resource> report = client.getInventoryResources(BEAN_TYPE, inventoryReport);
                Assert.assertEquals(report.size(), 7);
        }

        @Test
        public void checkBeanTypeContent()
        {
                //check bean with ID
                Resource bean1 = client.getInventoryResource(BEAN_TYPE, "controlDAO", inventoryReport);
                Assert.assertNotNull(bean1);
                Assert.assertEquals(bean1.getDefiningObject().equals("alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar"), true);

                //check bean without ID only with class, class should be displayed as id
                Resource bean2 = client.getInventoryResource(BEAN_TYPE, "org.alfresco.repo.domain.activities.ibatis.ActivityPostDAOImpl", inventoryReport);
                Assert.assertNotNull(bean2);
                Assert.assertEquals(bean2.getDefiningObject().equals("alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar"), true);

                //check bean without ID and class only with name, name should be displayed as id
                Resource bean3 = client.getInventoryResource(BEAN_TYPE, "sqlSessionTemplate", inventoryReport);
                Assert.assertNotNull(bean3);
                Assert.assertEquals(bean3.getDefiningObject().equals("alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar"), true);

                //check bean without ID,with class and name, name should be displayed as id
                Resource bean4 = client.getInventoryResource(BEAN_TYPE, "beanName", inventoryReport);
                Assert.assertNotNull(bean4);
                Assert.assertEquals(bean4.getDefiningObject().equals("alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar"), true);
        }

        @Test
        public void checkClassPathType()
        {
                List<Resource> report = client.getInventoryResources(CLASSPATH_ELEMENT_TYPE, inventoryReport);
                Assert.assertEquals(report.size(), 11);
        }

        @Test
        public void checkFileType()
        {
                List<Resource> report = client.getInventoryResources(FILE_TYPE, inventoryReport);
                Assert.assertEquals(report.size(), 10);

                Resource resource = client.getInventoryResource(FILE_TYPE, "META-INF/MANIFEST.MF", inventoryReport);
                Assert.assertEquals(resource.getDefiningObject().equals("META-INF/MANIFEST.MF"), true, "");
                Assert.assertEquals(resource.getId().equals("META-INF/MANIFEST.MF"), true, "");
        }

}
