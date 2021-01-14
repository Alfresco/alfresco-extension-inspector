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
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.zip.ZipEntry;

import org.alfresco.extension_inspector.inventory.data.classes.ClassDeprecated;
import org.alfresco.extension_inspector.inventory.data.classes.ClassWithAlfrescoApiAnnotation;
import org.alfresco.extension_inspector.inventory.data.classes.ClassWithAlfrescoApiAnnotationDeprecated;
import org.alfresco.extension_inspector.model.AlfrescoPublicApiResource;
import org.alfresco.extension_inspector.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PublicApiInventoryWorkerTest
{
    private static final String TEST_ALFRESCO_PUBLIC_API = "Lorg/alfresco/extension_inspector/inventory/data/classes/TestAlfrescoPublicApi;";
    @Spy
    private AlfrescoPublicApiInventoryWorker worker = new AlfrescoPublicApiInventoryWorker();

    @Test
    public void testWorkerHasProperType()
    {
        assertSame(Resource.Type.ALFRESCO_PUBLIC_API, worker.getType());
    }

    @Test
    public void testCanProcessClassEntry()
    {
        ZipEntry entry = new ZipEntry("AClass.class");
        assertFalse(worker.canProcessEntry(entry,"source"));

        entry = new ZipEntry("org/notalfresco/AClass.class");
        assertFalse(worker.canProcessEntry(entry,"source"));

        entry = new ZipEntry("org/alfresco/apackage/AClass.class");
        assertTrue(worker.canProcessEntry(entry,"source"));
    }

    @Test
    public void testCantProcessNonClassEntry()
    {
        ZipEntry entry = new ZipEntry("afile.xml");
        assertFalse(worker.canProcessEntry(entry,"source"));

        entry = new ZipEntry("org/alfresco/afile.xml");
        assertFalse(worker.canProcessEntry(entry,"source"));
    }

    @Test
    public void testClassWithAlfrescoPublicApiAnnotation() throws Exception
    {
        Class testClass = ClassWithAlfrescoApiAnnotation.class;

        ZipEntry zipEntry = new ZipEntry(getClassRelativePath(testClass));
        byte[] data = getClassData(testClass);

        doReturn(TEST_ALFRESCO_PUBLIC_API).when(worker).getPublicAnnotationType();
        Set<Resource> resources = worker.processZipEntry(zipEntry, data, "source");
        assertEquals(1, resources.size());

        assertTrue(resources.iterator().next() instanceof AlfrescoPublicApiResource);

        AlfrescoPublicApiResource resource = (AlfrescoPublicApiResource) resources.iterator().next();
        assertEquals(Resource.Type.ALFRESCO_PUBLIC_API, resource.getType());
        assertEquals(testClass.getName(), resource.getId(), testClass.getName() + " is part of AlfrescoPublicApi");
        assertFalse(resource.isDeprecated());
    }

    @Test
    public void testClassHasAlfrescoPublicApiAnnotationAndDeprecated() throws Exception
    {
        Class testClass = ClassWithAlfrescoApiAnnotationDeprecated.class;

        ZipEntry zipEntry = new ZipEntry(getClassRelativePath(testClass));
        byte[] data = getClassData(testClass);

        doReturn(TEST_ALFRESCO_PUBLIC_API).when(worker).getPublicAnnotationType();
        Set<Resource> resources = worker.processZipEntry(zipEntry, data, "source");
        assertEquals(1, resources.size());

        assertTrue(resources.iterator().next() instanceof AlfrescoPublicApiResource);

        AlfrescoPublicApiResource resource = (AlfrescoPublicApiResource) resources.iterator().next();
        assertEquals(testClass.getName(), resource.getId(), testClass.getName() + " is part of AlfrescoPublicApi");
        assertTrue(resource.isDeprecated());
    }

    @Test
    public void testClassNoAlfrescoPublicApiAnnotationAndDeprecated() throws Exception
    {
        Class testClass = ClassDeprecated.class;

        ZipEntry zipEntry = new ZipEntry(getClassRelativePath(testClass));
        byte[] data = getClassData(testClass);

        doReturn(TEST_ALFRESCO_PUBLIC_API).when(worker).getPublicAnnotationType();
        Set<Resource> resources = worker.processZipEntry(zipEntry, data, "source");
        assertEquals(0, resources.size());
    }

    private static byte[] getClassData(Class clazz) throws IOException
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

    private static String getClassRelativePath(Class clazz)
    {
        return clazz.getName().replace(".", "/") + ".class";
    }
}
