/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.worker;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;

import org.alfresco.ampalyser.inventory.EntryProcessor;
import org.alfresco.ampalyser.inventory.data.classes.*;
import org.alfresco.ampalyser.inventory.model.AlfrescoPublicApiResource;
import org.alfresco.ampalyser.inventory.model.ClasspathElementResource;
import org.alfresco.ampalyser.inventory.model.Resource;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class PublicApiInventoryWorkerTest
{
    @Spy
    private AlfrescoPublicApiInventoryWorker worker = new AlfrescoPublicApiInventoryWorker(new EntryProcessor());

    @BeforeEach
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        //Set the test annotation as the default AlfrescoPublicApi annotation in the worker
        Mockito.doReturn("Lorg/alfresco/ampalyser/inventory/data/classes/TestAlfrescoPublicApi;").
                when(worker).getPublicAnnotationType();
    }

    @Test
    public void testWorkerHasProperType()
    {
        assertSame(Resource.Type.ALFRESCO_PUBLIC_API, worker.getType());
    }

    @Test
    public void testCanProcessClassEntry()
    {
        ZipEntry entry = new ZipEntry("AClass.class");
        assertFalse("AlfrescoPublicApiInventoryWorker should not process classes which are not part of Alfresco code",
                worker.canProcessEntry(entry,"source"));

        entry = new ZipEntry("org/notalfresco/AClass.class");
        assertFalse("AlfrescoPublicApiInventoryWorker should not process classes which are not part of Alfresco code",
                worker.canProcessEntry(entry,"source"));

        entry = new ZipEntry("org/alfresco/apackage/AClass.class");
        assertTrue("AlfrescoPublicApiInventoryWorker should process classes which are part of Alfresco code",
                worker.canProcessEntry(entry,"source"));
    }

    @Test
    public void testCantProcessNonClassEntry()
    {
        ZipEntry entry = new ZipEntry("afile.xml");
        assertFalse("AlfrescoPublicApiInventoryWorker should process only class files",
                worker.canProcessEntry(entry,"source"));

        entry = new ZipEntry("org/alfresco/afile.xml");
        assertFalse("AlfrescoPublicApiInventoryWorker should process only class files",
                worker.canProcessEntry(entry,"source"));
    }

    @Test
    public void testClassWithAlfrescoPublicApiAnnotation() throws Exception
    {
        Class testClass = ClassWithAlfrescoApiAnnotation.class;

        ZipEntry zipEntry = new ZipEntry(getClassRelativePath(testClass));
        byte[] data = getClassData(testClass);

        List<Resource> resources = worker.processZipEntry(zipEntry, data, "source");
        assertEquals(1, resources.size());

        Assert.assertTrue(resources.get(0) instanceof AlfrescoPublicApiResource);
        AlfrescoPublicApiResource resource = (AlfrescoPublicApiResource) resources.get(0);
        Assert.assertEquals(Resource.Type.ALFRESCO_PUBLIC_API, resource.getType());
        assertEquals(testClass.getName() + " is part of AlfrescoPublicApi", testClass.getName(), resource.getId());
    }

    @Test
    public void testClassHasAlfrescoPublicApiAnnotationAndDeprecated() throws Exception
    {
        Class testClass = ClassWithAlfrescoApiAnnotationDeprecated.class;

        ZipEntry zipEntry = new ZipEntry(getClassRelativePath(testClass));
        byte[] data = getClassData(testClass);

        List<Resource> resources = worker.processZipEntry(zipEntry, data, "source");
        assertEquals(1, resources.size());

        Assert.assertTrue(resources.get(0) instanceof AlfrescoPublicApiResource);
        AlfrescoPublicApiResource resource = (AlfrescoPublicApiResource)resources.get(0);
        assertEquals(testClass.getName() + " is part of AlfrescoPublicApi", testClass.getName(), resource.getId());
        assertTrue(testClass.getName() + " is deprecated", resource.isDeprecated());
    }

    @Test
    public void testClassNoAlfrescoPublicApiAnnotationAndDeprecated() throws Exception
    {
        Class testClass = ClassDeprecated.class;

        ZipEntry zipEntry = new ZipEntry(getClassRelativePath(testClass));
        byte[] data = getClassData(testClass);

        List<Resource> resources = worker.processZipEntry(zipEntry, data, "source");
        assertEquals(0, resources.size());
    }

    private byte[] getClassData(Class clazz) throws IOException
    {
        String name = clazz.getName();
        int i = name.lastIndexOf('.');
        if (i > 0)
        {
            name = name.substring(i + 1);
        }
        InputStream clsStream = clazz.getResourceAsStream(name + ".class");
        return  clsStream.readAllBytes();
    }

    private String getClassRelativePath(Class clazz)
    {
        String path = clazz.getName().replaceAll("\\.", "/") + ".class";
        return path;
    }
}
