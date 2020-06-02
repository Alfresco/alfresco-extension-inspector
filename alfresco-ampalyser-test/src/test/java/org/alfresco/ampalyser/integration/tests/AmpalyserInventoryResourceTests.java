package org.alfresco.ampalyser.integration.tests;

import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.models.Resource;
import org.alfresco.ampalyser.util.TestResource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class AmpalyserInventoryResourceTests extends AmpalyserInventoryTests
{
        CommandOutput cmdOut;

        @BeforeClass
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
                cmdOut = client.runInventoryAnalyserCommand(cmdOptions);
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
        public void checkBeanTypeContent()
        {
                //check bean with ID
                Resource bean = client.getInventoryResource(BEAN_TYPE, "controlDAO", inventoryReport);
                Assert.assertNotNull(bean);
                Assert.assertEquals(bean.getDefiningObject().equals("alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar"), true);

                //check bean without ID only with class, class should be displayed as id
                bean = client.getInventoryResource(BEAN_TYPE, "org.alfresco.repo.domain.activities.ibatis.ActivityPostDAOImpl", inventoryReport);
                Assert.assertNotNull(bean);
                Assert.assertEquals(bean.getDefiningObject().equals("alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar"), true);

                //check bean without ID and class only with name, name should be displayed as id
                bean = client.getInventoryResource(BEAN_TYPE, "sqlSessionTemplate", inventoryReport);
                Assert.assertNotNull(bean);
                Assert.assertEquals(bean.getDefiningObject().equals("alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar"), true);

                //check bean without ID,with class and name, name should be displayed as id
                bean = client.getInventoryResource(BEAN_TYPE, "beanName", inventoryReport);
                Assert.assertNotNull(bean);
                Assert.assertEquals(bean.getDefiningObject().equals("alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar"), true);
        }

        @Test
        public void testInvalidBeans()
        {
                // Check that invalid xml file is not loaded and and error is displayed in command output
                Assert.assertTrue(cmdOut.containsMessage(INVALID_XML_MESSAGE + INVALID_XML), "Invalid xml message not displayed in command output");
                Resource bean = client.getInventoryResource(BEAN_TYPE, "invalidXML1", inventoryReport);
                Assert.assertNull(bean);
        }

        @Test
        public void testClassPathType()
        {
                List<Resource> classPathRs = client.getInventoryResources(CLASSPATH_ELEMENT_TYPE, inventoryReport);
                Assert.assertEquals(classPathRs.size(), 12);

                Resource classPathResource = client.getInventoryResource(CLASSPATH_ELEMENT_TYPE, "org/alfresco/repo/node/NodeServicePolicies.class", inventoryReport);
                Assert.assertNotNull(classPathResource);
                Assert.assertEquals(classPathResource.getDefiningObject(), "WEB-INF/lib/alfresco-repository-0.0.1.jar");

                classPathResource = client.getInventoryResource(CLASSPATH_ELEMENT_TYPE, "log4j.properties", inventoryReport);
                Assert.assertNotNull(classPathResource);
                Assert.assertEquals(classPathResource.getDefiningObject(), "WEB-INF/classes/log4j.properties");
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
