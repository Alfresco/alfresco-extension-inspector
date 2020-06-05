/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.integration.tests;

import org.alfresco.ampalyser.model.AlfrescoPublicApiResource;
import org.alfresco.ampalyser.model.Resource;
import org.alfresco.ampalyser.util.TestResource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class AmpalyserInventoryResourceTests extends AmpalyserInventoryTests
{
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
                List<Resource> publicApiResources = client.retrieveInventoryResources(Resource.Type.ALFRESCO_PUBLIC_API, inventoryReport);
                Assert.assertEquals(publicApiResources.size(), 2);

                AlfrescoPublicApiResource publicApiRs = (AlfrescoPublicApiResource) client
                        .retrieveInventoryResource(Resource.Type.ALFRESCO_PUBLIC_API, "org.alfresco.repo.node.NodeServicePolicies", inventoryReport);
                Assert.assertNotNull(publicApiRs);
                Assert.assertFalse(publicApiRs.isDeprecated());

                publicApiRs = (AlfrescoPublicApiResource) client
                        .retrieveInventoryResource(Resource.Type.ALFRESCO_PUBLIC_API, "org.alfresco.repo.content.transform.TransformerConfig", inventoryReport);
                Assert.assertNotNull(publicApiRs);
                Assert.assertTrue(publicApiRs.isDeprecated());
        }

        @Test
        public void checkBeanTypeContent()
        {
                //check bean with ID
                Resource bean = client.retrieveInventoryResource(Resource.Type.BEAN, "controlDAO", inventoryReport);
                Assert.assertNotNull(bean);
                Assert.assertEquals(bean.getDefiningObject(), "alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar");

                //check bean without ID only with class, class should be displayed as id
                bean = client.retrieveInventoryResource(Resource.Type.BEAN, "org.alfresco.repo.domain.activities.ibatis.ActivityPostDAOImpl", inventoryReport);
                Assert.assertNotNull(bean);
                Assert.assertEquals(bean.getDefiningObject(), "alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar");

                //check bean without ID and class only with name, name should be displayed as id
                bean = client.retrieveInventoryResource(Resource.Type.BEAN, "sqlSessionTemplate", inventoryReport);
                Assert.assertNotNull(bean);
                Assert.assertEquals(bean.getDefiningObject(), "alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar");

                //check bean without ID,with class and name, name should be displayed as id
                bean = client.retrieveInventoryResource(Resource.Type.BEAN, "beanName", inventoryReport);
                Assert.assertNotNull(bean);
                Assert.assertEquals(bean.getDefiningObject(), "alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar");
        }

        @Test
        public void testInvalidBeans()
        {
                // Check that invalid xml file is not loaded and and error is displayed in command output
                Assert.assertTrue(cmdOut.containsMessage(INVALID_XML_MESSAGE + INVALID_XML), "Invalid xml message not displayed in command output");
                Resource bean = client.retrieveInventoryResource(Resource.Type.BEAN, "invalidXML1", inventoryReport);
                Assert.assertNull(bean);
        }

        @Test
        public void testClassPathType()
        {
                List<Resource> classPathRs = client.retrieveInventoryResources(Resource.Type.CLASSPATH_ELEMENT, inventoryReport);
                Assert.assertEquals(classPathRs.size(), 12);

                Resource classPathResource = client.retrieveInventoryResource(Resource.Type.CLASSPATH_ELEMENT, "org/alfresco/repo/node/NodeServicePolicies.class", inventoryReport);
                Assert.assertNotNull(classPathResource);
                Assert.assertEquals(classPathResource.getDefiningObject(), "WEB-INF/lib/alfresco-repository-0.0.1.jar");

                classPathResource = client.retrieveInventoryResource(Resource.Type.CLASSPATH_ELEMENT, "log4j.properties", inventoryReport);
                Assert.assertNotNull(classPathResource);
                Assert.assertEquals(classPathResource.getDefiningObject(), "WEB-INF/classes/log4j.properties");
        }

        @Test
        public void checkFileType()
        {
                List<Resource> report = client.retrieveInventoryResources(Resource.Type.FILE, inventoryReport);
                Assert.assertEquals(report.size(), 10);

                Resource resource = client.retrieveInventoryResource(Resource.Type.FILE, "META-INF/MANIFEST.MF", inventoryReport);
                Assert.assertEquals(resource.getDefiningObject(), "META-INF/MANIFEST.MF");
                Assert.assertEquals(resource.getId(), "META-INF/MANIFEST.MF");
        }

}
