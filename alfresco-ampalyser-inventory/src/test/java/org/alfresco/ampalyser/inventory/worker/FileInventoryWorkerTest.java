/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.worker;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.zip.ZipEntry;

import org.alfresco.ampalyser.inventory.EntryProcessor;
import org.alfresco.ampalyser.inventory.model.FileResource;
import org.alfresco.ampalyser.inventory.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FileInventoryWorkerTest
{
    @Mock
    private EntryProcessor processor;
    @InjectMocks
    private FileInventoryWorker fileInventoryWorker;

    @Test
    public void testProcessTxtEntry()
    {
        ZipEntry entry = new ZipEntry("TestEntry.txt");
        assertTrue(fileInventoryWorker.canProcessEntry(entry, entry.getName()));

        List<Resource> resourceList = fileInventoryWorker
            .processZipEntry(entry, null, entry.getName());
        assertTrue(!resourceList.isEmpty());
        assertEquals(1, resourceList.size());
        assertTrue(resourceList.get(0) instanceof FileResource);

        assertEquals(Resource.Type.FILE, resourceList.get(0).getType());
        assertEquals(entry.getName(), resourceList.get(0).getId());
        assertEquals(entry.getName(), resourceList.get(0).getDefiningObject());
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

        assertEquals(emptyList(), fileInventoryWorker.processZipEntry(entry, null, definingObject));
    }

    @Test
    public void testCannotProcessDirectory()
    {
        assertFalse(fileInventoryWorker.canProcessEntry(new ZipEntry("directory/"), "directory/"));
    }
}
