/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.printers.ConflictPrinter.joinExtensionDefiningObjs;
import static org.alfresco.ampalyser.analyser.printers.ConflictPrinter.joinWarResourceIds;
import static org.alfresco.ampalyser.analyser.printers.ConflictPrinter.joinWarVersions;
import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.alfresco.ampalyser.analyser.result.BeanOverwriteConflict;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.model.BeanResource;

import org.alfresco.ampalyser.model.FileResource;
import org.junit.Test;

public class ConflictPrinterTest
{
    @Test
    public void testJoinWarVersions()
    {
        BeanResource extBean1 = new BeanResource("bean1", "default_context.xml",
            "org.alfresco.Dummy");
        BeanResource extBean11 = new BeanResource("bean1", "another_context.xml",
            "org.alfresco.Dummy");
        BeanResource warBean1 = new BeanResource("bean1", "default_war_context.xml",
            "org.alfresco.Dummy");

        Conflict c1 = new BeanOverwriteConflict(extBean1, warBean1, "6.0.0.2");
        Conflict c2 = new BeanOverwriteConflict(extBean11, warBean1, "6.0.0.2");
        Conflict c3 = new BeanOverwriteConflict(extBean1, warBean1, "6.0.0.1");
        Conflict c4 = new BeanOverwriteConflict(extBean1, warBean1, "6.0.0.5");
        Conflict c5 = new BeanOverwriteConflict(extBean1, warBean1, "6.0.2");

        Set<Conflict> conflicts = Set.of(c1, c2, c3, c4, c5);

        assertEquals("6.0.0.1, 6.0.0.2, 6.0.0.5, 6.0.2", joinWarVersions(conflicts));
    }

    @Test
    public void testJoinWarResourceIds()
    {
        FileResource extFile1 = new FileResource("file1.txt", "file1.txt");
        FileResource warFile1 = new FileResource("file1.txt", "file1.txt");

        FileResource extFile2 = new FileResource("file2.txt", "file2.txt");
        FileResource warFile2 = new FileResource("file2.txt", "file2.txt");
        
        Conflict c1 = new BeanOverwriteConflict(extFile1, warFile1, "6.0.0.2");
        Conflict c2 = new BeanOverwriteConflict(extFile2, warFile2, "6.0.0.2");
        Conflict c3 = new BeanOverwriteConflict(extFile1, warFile1, "6.0.0.1");
        Conflict c4 = new BeanOverwriteConflict(extFile1, warFile1, "6.0.0.5");
        Conflict c5 = new BeanOverwriteConflict(extFile2, warFile2, "6.0.2");

        Set<Conflict> conflicts = Set.of(c1, c2, c3, c4, c5);

        assertEquals("file1.txt\nfile2.txt", joinWarResourceIds(conflicts));
    }

    @Test
    public void testJoinExtensionDefiningObjs()
    {
        BeanResource extBean1 = new BeanResource("bean1", "default_context.xml",
            "org.alfresco.Dummy");
        BeanResource extBean2 = new BeanResource("bean2", "default_context.xml",
            "org.alfresco.Dummy");
        BeanResource extBean22 = new BeanResource("bean2", "another_context.xml",
            "org.alfresco.Dummy");

        BeanResource warBean1 = new BeanResource("bean1", "another_war_context.xml",
            "org.alfresco.Dummy");
        BeanResource warBean2 = new BeanResource("bean2", "default_war_context.xml",
            "org.alfresco.Dummy");

        Conflict c1 = new BeanOverwriteConflict(extBean2, warBean2, "6.0.0.2");
        Conflict c2 = new BeanOverwriteConflict(extBean22, warBean2, "6.0.0.2");
        Conflict c3 = new BeanOverwriteConflict(extBean1, warBean1, "6.0.0.1");
        Conflict c4 = new BeanOverwriteConflict(extBean2, warBean2, "6.0.0.5");

        Set<Conflict> conflicts = Set.of(c1, c2, c3, c4);

        assertEquals("another_context.xml, default_context.xml",
            joinExtensionDefiningObjs(conflicts));
    }
}
