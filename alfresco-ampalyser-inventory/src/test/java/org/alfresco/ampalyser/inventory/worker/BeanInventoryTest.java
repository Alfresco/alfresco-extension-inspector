/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.inventory.worker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.zip.ZipEntry;

import org.alfresco.ampalyser.inventory.EntryProcessor;
import org.alfresco.ampalyser.model.BeanResource;
import org.alfresco.ampalyser.model.Resource;
import org.junit.jupiter.api.Test;

/**
 * @author Lucian Tuca
 * created on 15/05/2020
 */

public class BeanInventoryTest
{
    private BeanInventoryWorker beanInventoryWorker = new BeanInventoryWorker(new EntryProcessor());

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
            + "    <bean id='bean-with-id' name='bean-with-name' class='org.alfresco.ampalyser.inventory.Test'></bean>"
            + "</beans>").getBytes();

        String definingObject = "unicorn-jar-for-tests.jar";
        List<Resource> resources = beanInventoryWorker
            .processZipEntry(zipEntry, data, definingObject);

        assertEquals(1, resources.size());
        assertTrue(resources.get(0) instanceof BeanResource);

        BeanResource br = (BeanResource) resources.get(0);
        assertEquals("bean-with-id", br.getId());
        assertEquals(zipEntry.getName() + "@" + definingObject, br.getDefiningObject());
    }

    @Test
    public void happyFlowForBeansWithoutId()
    {
        ZipEntry zipEntry = new ZipEntry("file.xml");
        byte[] data = (
            "<beans>"
            + "    <bean name='bean-with-name' class='org.alfresco.ampalyser.inventory.Test'></bean>"
            + "</beans>").getBytes();

        String definingObject = "unicorn-jar-for-tests.jar";
        List<Resource> resources = beanInventoryWorker
            .processZipEntry(zipEntry, data, definingObject);

        assertEquals(1, resources.size());
        assertTrue(resources.get(0) instanceof BeanResource);

        BeanResource br = (BeanResource) resources.get(0);
        assertEquals("bean-with-name", br.getId());
        assertEquals(zipEntry.getName() + "@" + definingObject, br.getDefiningObject());
    }

    @Test
    public void happyFlowForBeansWithoutIdAndWithoutName()
    {
        ZipEntry zipEntry = new ZipEntry("file.xml");
        byte[] data = (
            "<beans>"
            + "    <bean class='org.alfresco.ampalyser.inventory.Test'></bean>"
            + "</beans>").getBytes();

        String definingObject = "unicorn-jar-for-tests.jar";
        List<Resource> resources = beanInventoryWorker
            .processZipEntry(zipEntry, data, definingObject);

        assertEquals(1, resources.size());
        assertTrue(resources.get(0) instanceof BeanResource);

        BeanResource br = (BeanResource) resources.get(0);
        assertEquals("org.alfresco.ampalyser.inventory.Test", br.getId());
        assertEquals(zipEntry.getName() + "@" + definingObject, br.getDefiningObject());
    }

    @Test
    public void happyFlowForBeansWithoutIdAndWithoutNameAndAnonymousBeans()
    {
        ZipEntry zipEntry = new ZipEntry("file.xml");
        byte[] data = (
            "<beans>"
            + "    <bean class='org.alfresco.ampalyser.inventory.Test'></bean>"
            + "    <bean parent='only-parent-defined'>I should be anonymous</bean>"
            + "</beans>").getBytes();

        String definingObject = "unicorn-jar-for-tests.jar";
        List<Resource> resources = beanInventoryWorker
            .processZipEntry(zipEntry, data, definingObject);

        assertEquals(1, resources.size());
        assertTrue(resources.get(0) instanceof BeanResource);

        BeanResource br = (BeanResource) resources.get(0);
        assertEquals("org.alfresco.ampalyser.inventory.Test", br.getId());
        assertEquals(zipEntry.getName() + "@" + definingObject, br.getDefiningObject());
    }
}
