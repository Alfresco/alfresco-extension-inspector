/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.worker;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.zip.ZipEntry;

import org.alfresco.ampalyser.inventory.EntryProcessor;
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
        assertTrue(
            worker.canProcessEntry(new ZipEntry("WEB-INF/classes/TestEntry.txt"), "TestEntry.txt"));
    }

    @Test
    public void testCannotProcessEntryIfNotFromJarOrFromWebinfClasses()
    {
        assertFalse(
            worker.canProcessEntry(new ZipEntry("TestEntry.txt"), "TestEntry.txt"));
    }

    @Test
    public void testCannotProcessJarEntry()
    {
        assertFalse(
            worker.canProcessEntry(new ZipEntry("testEntry.jar"), "testEntry.jar"));
    }

    @Test
    public void testCanProcessEntryFromJar()
    {
        assertTrue(
            worker.canProcessEntry(new ZipEntry("TestEntry.class"), "testEntry.jar"));
    }

    @Test
    public void testCannotProcessDirectory()
    {
        assertFalse(worker.canProcessEntry(new ZipEntry("directory/"), "directory/"));
    }
}
