/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.alfresco.extension_inspector.inventory.worker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.zip.ZipEntry;

import org.alfresco.extension_inspector.model.BeanResource;
import org.alfresco.extension_inspector.model.Resource;
import org.junit.jupiter.api.Test;

/**
 * @author Lucian Tuca
 */

public class BeanInventoryTest
{
    private BeanInventoryWorker beanInventoryWorker = new BeanInventoryWorker();

    @Test
    public void testBeanInventoryWorkerReturnsCorrectType()
    {
        assertSame(Resource.Type.BEAN, beanInventoryWorker.getType());
    }

    @Test
    public void testBIWCanProcessXml()
    {
        ZipEntry zipEntry = new ZipEntry("file.xml");

        assertTrue(beanInventoryWorker.canProcessEntry(zipEntry,"Luke, I'm your father."));
    }

    @Test
    public void testBIWCantProcessJsons()
    {
        ZipEntry zipEntry = new ZipEntry("file.json");

        assertFalse(beanInventoryWorker.canProcessEntry(zipEntry,"Luke, I'm your father."));
    }

    @Test
    public void happyFlowForBeans()
    {
        ZipEntry zipEntry = new ZipEntry("file.xml");
        byte[] data = (
            "<beans>"
            + "    <bean id='bean-with-id' name='bean-with-name' class='org.alfresco.extension_inspector.inventory.Test'></bean>"
            + "</beans>").getBytes();

        String definingObject = "unicorn-jar-for-tests.jar";
        Set<Resource> resources = beanInventoryWorker
            .processZipEntry(zipEntry, data, definingObject);

        assertEquals(1, resources.size());
        assertTrue(resources.iterator().next() instanceof BeanResource);

        BeanResource br = (BeanResource) resources.iterator().next();
        assertEquals("bean-with-id", br.getId());
        assertEquals(zipEntry.getName() + "@" + definingObject, br.getDefiningObject());
    }

    @Test
    public void happyFlowForBeansWithoutId()
    {
        ZipEntry zipEntry = new ZipEntry("file.xml");
        byte[] data = (
            "<beans>"
            + "    <bean name='bean-with-name' class='org.alfresco.extension_inspector.inventory.Test'></bean>"
            + "</beans>").getBytes();

        String definingObject = "unicorn-jar-for-tests.jar";
        Set<Resource> resources = beanInventoryWorker
            .processZipEntry(zipEntry, data, definingObject);

        assertEquals(1, resources.size());
        assertTrue(resources.iterator().next() instanceof BeanResource);

        BeanResource br = (BeanResource) resources.iterator().next();
        assertEquals("bean-with-name", br.getId());
        assertEquals(zipEntry.getName() + "@" + definingObject, br.getDefiningObject());
    }

    @Test
    public void happyFlowForBeansWithoutIdAndWithoutName()
    {
        ZipEntry zipEntry = new ZipEntry("file.xml");
        byte[] data = (
            "<beans>"
            + "    <bean class='org.alfresco.extension_inspector.inventory.Test'></bean>"
            + "</beans>").getBytes();

        String definingObject = "unicorn-jar-for-tests.jar";
        Set<Resource> resources = beanInventoryWorker
            .processZipEntry(zipEntry, data, definingObject);

        assertEquals(1, resources.size());
        assertTrue(resources.iterator().next() instanceof BeanResource);

        BeanResource br = (BeanResource) resources.iterator().next();
        assertEquals("org.alfresco.extension_inspector.inventory.Test", br.getId());
        assertEquals(zipEntry.getName() + "@" + definingObject, br.getDefiningObject());
    }

    @Test
    public void happyFlowForBeansWithoutIdAndWithoutNameAndAnonymousBeans()
    {
        ZipEntry zipEntry = new ZipEntry("file.xml");
        byte[] data = (
            "<beans>"
            + "    <bean class='org.alfresco.extension_inspector.inventory.Test'></bean>"
            + "    <bean parent='only-parent-defined'>I should be anonymous</bean>"
            + "</beans>").getBytes();

        String definingObject = "unicorn-jar-for-tests.jar";
        Set<Resource> resources = beanInventoryWorker
            .processZipEntry(zipEntry, data, definingObject);

        assertEquals(1, resources.size());
        assertTrue(resources.iterator().next() instanceof BeanResource);

        BeanResource br = (BeanResource) resources.iterator().next();
        assertEquals("org.alfresco.extension_inspector.inventory.Test", br.getId());
        assertEquals(zipEntry.getName() + "@" + definingObject, br.getDefiningObject());
    }
}
