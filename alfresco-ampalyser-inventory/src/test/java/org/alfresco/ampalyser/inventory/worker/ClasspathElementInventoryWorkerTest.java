/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.worker;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.zip.ZipEntry;

import org.alfresco.ampalyser.inventory.EntryProcessor;
import org.alfresco.ampalyser.inventory.model.ClasspathElementResource;
import org.alfresco.ampalyser.inventory.model.Resource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ClasspathElementInventoryWorkerTest
{
    @Mock
    private EntryProcessor processor;
    @InjectMocks
    private ClasspathElementInventoryWorker worker;

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCanProcessTxtEntryFromWebInfClasses()
    {
        ZipEntry entry = new ZipEntry("WEB-INF/classes/TestEntry.txt");
        assertTrue(worker.canProcessEntry(entry, entry.getName()));

        List<Resource> resourceList = worker.processZipEntry(entry, null, entry.getName());
        assertTrue(!resourceList.isEmpty());
        assertEquals(1, resourceList.size());
        assertTrue(resourceList.get(0) instanceof ClasspathElementResource);

        assertEquals(Resource.Type.CLASSPATH_ELEMENT, resourceList.get(0).getType());
        assertEquals("TestEntry.txt", resourceList.get(0).getId());
        assertEquals(entry.getName(), resourceList.get(0).getDefiningObject());
    }

    @Test
    public void testCannotProcessEntryIfNotFromJarOrFromWebInfClasses()
    {
        ZipEntry entry = new ZipEntry("TestEntry.txt");
        assertFalse(worker.canProcessEntry(entry, "TestEntry.txt"));

        assertEquals(emptyList(), worker.processZipEntry(entry, null, entry.getName()));
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
