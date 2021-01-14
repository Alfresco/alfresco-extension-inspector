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

import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.zip.ZipEntry;

import org.alfresco.extension_inspector.model.ClasspathElementResource;
import org.alfresco.extension_inspector.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClasspathElementInventoryWorkerTest
{
    @InjectMocks
    private ClasspathElementInventoryWorker worker;

    @Test
    public void testCanProcessTxtEntryFromWebInfClasses()
    {
        ZipEntry entry = new ZipEntry("WEB-INF/classes/TestEntry.txt");
        assertTrue(worker.canProcessEntry(entry, entry.getName()));

        Set<Resource> resourceSet = worker.processZipEntry(entry, null, entry.getName());
        assertTrue(!resourceSet.isEmpty());
        assertEquals(1, resourceSet.size());
        assertTrue(resourceSet.iterator().next() instanceof ClasspathElementResource);

        assertEquals(Resource.Type.CLASSPATH_ELEMENT, resourceSet.iterator().next().getType());
        assertEquals("/TestEntry.txt", resourceSet.iterator().next().getId());
        assertEquals("/" + entry.getName(), resourceSet.iterator().next().getDefiningObject());
    }
    
    @Test
    public void testCanProcessTxtEntryFromExtension()
    {
        ZipEntry entry = new ZipEntry("config/TestEntry.txt");
        assertTrue(worker.canProcessEntry(entry, entry.getName()));

        entry = new ZipEntry("alfresco/TestEntry.txt");
        assertTrue(worker.canProcessEntry(entry, entry.getName()));

        entry = new ZipEntry("TestEntry.class");
        assertTrue(worker.canProcessEntry(entry, entry.getName()));
    }

    @Test
    public void testCannotProcessEntryIfNotFromJarOrFromWebInfClassesOrFromExtension()
    {
        ZipEntry entry = new ZipEntry("TestEntry.txt");
        assertFalse(worker.canProcessEntry(entry, "TestEntry.txt"));

        assertEquals(emptySet(), worker.processZipEntry(entry, null, entry.getName()));
    }

    @Test
    public void testCannotProcessJarEntry()
    {
        assertFalse(worker.canProcessEntry(new ZipEntry("testEntry.jar"), "testEntry.jar"));
    }

    @Test
    public void testCanProcessEntryFromJar()
    {
        assertTrue(worker.canProcessEntry(new ZipEntry("TestEntry.class"), "testEntry.jar"));
    }

    @Test
    public void testCannotProcessDirectory()
    {
        assertFalse(worker.canProcessEntry(new ZipEntry("directory/"), "directory/"));
    }
}
