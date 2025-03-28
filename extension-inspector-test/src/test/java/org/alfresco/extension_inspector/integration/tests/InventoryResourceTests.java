/*
 * Copyright 2021 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfresco.extension_inspector.integration.tests;

import static org.alfresco.extension_inspector.model.Resource.Type.ALFRESCO_PUBLIC_API;
import static org.alfresco.extension_inspector.model.Resource.Type.BEAN;
import static org.alfresco.extension_inspector.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.alfresco.extension_inspector.model.Resource.Type.FILE;
import static org.alfresco.extension_inspector.util.TestResource.INVALID_XML;
import static org.alfresco.extension_inspector.util.TestResource.INVALID_XML_MESSAGE;
import static org.alfresco.extension_inspector.util.TestResource.SUCCESS_MESSAGE;
import static org.alfresco.extension_inspector.util.TestResource.getTestInventoryReport;
import static org.alfresco.extension_inspector.util.TestResource.getTestResourcePath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.alfresco.extension_inspector.ExtensionInspectorClient;
import org.alfresco.extension_inspector.model.AlfrescoPublicApiResource;
import org.alfresco.extension_inspector.model.Resource;
import org.alfresco.extension_inspector.models.CommandOutput;
import org.alfresco.extension_inspector.util.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(classes = AppConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class InventoryResourceTests extends AbstractTestNGSpringContextTests
{
    @Autowired
    private ExtensionInspectorClient client;

    private File inventoryReport;
    private CommandOutput cmdOut;

    @BeforeClass
    public void executeCommand()
    {
        // Delete inventory report if exists
        inventoryReport = getTestInventoryReport();
        if (inventoryReport.exists())
        {
            inventoryReport.delete();
        }

        final String warResourcePath = getTestResourcePath("inventoryTest.war");
        final List<String> cmdOptions = List.of(warResourcePath);

        // Generate new inventory report
        cmdOut = client.runExtensionInspectorInventoryCommand(cmdOptions);
        assertEquals(cmdOut.getExitCode(), 0);
        assertTrue(cmdOut.isInOutput(SUCCESS_MESSAGE), "Inventory report has not been generated");

        // Get the new inventory report
        inventoryReport = getTestInventoryReport();
    }

    @Test
    public void testJsonReportExists()
    {
        assertTrue(inventoryReport.exists());
    }

    @Test
    public void testPublicApiAnnotation()
    {
        Set<Resource> publicApiResources = client.retrieveInventoryResources(ALFRESCO_PUBLIC_API, inventoryReport);
        assertEquals(publicApiResources.size(), 4);

        AlfrescoPublicApiResource publicApiRs = (AlfrescoPublicApiResource) client
            .retrieveInventoryResource(ALFRESCO_PUBLIC_API, "org.alfresco.repo.node.NodeServicePolicies", inventoryReport);
        assertNotNull(publicApiRs);
        assertFalse(publicApiRs.isDeprecated());

        publicApiRs = (AlfrescoPublicApiResource) client
            .retrieveInventoryResource(ALFRESCO_PUBLIC_API, "org.alfresco.repo.content.transform.TransformerConfig", inventoryReport);
        assertNotNull(publicApiRs);
        assertTrue(publicApiRs.isDeprecated());
    }

    @Test
    public void checkBeanTypeContent()
    {
        //check bean with ID
        Resource bean = client.retrieveInventoryResource(BEAN, "controlDAO", inventoryReport);
        assertNotNull(bean);
        assertEquals(bean.getDefiningObject(), "alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar");

        //check bean without ID only with class, class should be displayed as id
        bean = client.retrieveInventoryResource(BEAN, "org.alfresco.repo.domain.activities.ibatis.ActivityPostDAOImpl", inventoryReport);
        assertNotNull(bean);
        assertEquals(bean.getDefiningObject(), "alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar");

        //check bean without ID and class only with name, name should be displayed as id
        bean = client.retrieveInventoryResource(BEAN, "sqlSessionTemplate", inventoryReport);
        assertNotNull(bean);
        assertEquals(bean.getDefiningObject(), "alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar");

        //check bean without ID,with class and name, name should be displayed as id
        bean = client.retrieveInventoryResource(BEAN, "beanName", inventoryReport);
        assertNotNull(bean);
        assertEquals(bean.getDefiningObject(), "alfresco/dao/dao-context.xml@WEB-INF/lib/alfresco-repository-0.0.1.jar");
    }

    @Test
    public void testInvalidBeans()
    {
        // Check that invalid xml file is not loaded and and error is displayed in command output
        assertTrue(cmdOut.isInOutput(INVALID_XML_MESSAGE + INVALID_XML), "Invalid xml message not displayed in command output");
        Resource bean = client.retrieveInventoryResource(BEAN, "/invalidXML1", inventoryReport);
        assertNull(bean);
    }

    @Test
    public void testClassPathType()
    {
        Set<Resource> classPathRs = client.retrieveInventoryResources(CLASSPATH_ELEMENT, inventoryReport);
        assertEquals(classPathRs.size(), 12);

        Resource classPathResource = client.retrieveInventoryResource(CLASSPATH_ELEMENT, "/org/alfresco/repo/node/NodeServicePolicies.class", inventoryReport);
        assertNotNull(classPathResource);
        assertEquals(classPathResource.getDefiningObject(), "/WEB-INF/lib/alfresco-repository-0.0.1.jar");

        classPathResource = client.retrieveInventoryResource(CLASSPATH_ELEMENT, "/log4j.properties", inventoryReport);
        assertNotNull(classPathResource);
        assertEquals(classPathResource.getDefiningObject(), "/WEB-INF/classes/log4j.properties");
    }

    @Test
    public void checkFileType()
    {
        Set<Resource> report = client.retrieveInventoryResources(FILE, inventoryReport);
        assertEquals(report.size(), 9);

        Resource resource = client.retrieveInventoryResource(FILE, "/WEB-INF/web.xml", inventoryReport);
        assertEquals(resource.getDefiningObject(), "/WEB-INF/web.xml");
        assertEquals(resource.getId(), "/WEB-INF/web.xml");
    }
}
