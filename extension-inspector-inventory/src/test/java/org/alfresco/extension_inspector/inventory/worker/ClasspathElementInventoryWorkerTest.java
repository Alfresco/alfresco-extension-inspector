/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
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