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

import org.alfresco.extension_inspector.model.FileResource;
import org.alfresco.extension_inspector.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FileInventoryWorkerTest
{
    @InjectMocks
    private FileInventoryWorker fileInventoryWorker;

    @Test
    public void testProcessTxtEntry()
    {
        ZipEntry entry = new ZipEntry("TestEntry.txt");
        assertTrue(fileInventoryWorker.canProcessEntry(entry, entry.getName()));

        Set<Resource> resourceSet = fileInventoryWorker
            .processZipEntry(entry, null, entry.getName());
        assertFalse(resourceSet.isEmpty());
        assertEquals(1, resourceSet.size());
        assertTrue(resourceSet.iterator().next() instanceof FileResource);

        assertEquals(Resource.Type.FILE, resourceSet.iterator().next().getType());
        assertEquals("/" + entry.getName(), resourceSet.iterator().next().getId());
        assertEquals("/" + entry.getName(), resourceSet.iterator().next().getDefiningObject());
    }

    @Test
    public void testCanProcessJarEntry()
    {
        assertTrue(
            fileInventoryWorker.canProcessEntry(new ZipEntry("testEntry.jar"), "testEntry.jar"));
    }

    @Test
    public void testCannotProcessEntryFromJar()
    {
        ZipEntry entry = new ZipEntry("TestEntry.class");
        String definingObject = "testEntry.jar";
        assertFalse(fileInventoryWorker.canProcessEntry(entry, definingObject));

        assertEquals(emptySet(), fileInventoryWorker.processZipEntry(entry, null, definingObject));
    }

    @Test
    public void testCannotProcessDirectory()
    {
        assertFalse(fileInventoryWorker.canProcessEntry(new ZipEntry("directory/"), "directory/"));
    }
}
