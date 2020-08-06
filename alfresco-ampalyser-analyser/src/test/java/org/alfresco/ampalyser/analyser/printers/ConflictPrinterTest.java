/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static java.util.Collections.emptyMap;
import static org.alfresco.ampalyser.analyser.printers.ConflictPrinter.joinExtensionDefiningObjs;
import static org.alfresco.ampalyser.analyser.printers.ConflictPrinter.joinWarResourceDefiningObjs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.alfresco.ampalyser.analyser.result.BeanOverwriteConflict;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.FileOverwriteConflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.alfresco.ampalyser.model.BeanResource;
import org.alfresco.ampalyser.model.FileResource;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConflictPrinterTest
{
    @Mock
    private WarInventoryReportStore store;
    @InjectMocks
    private BeanOverwriteConflictPrinter printer;

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);

        when(store.allKnownVersions()).thenReturn(
            Set.of("5.2.0", "5.2.4", "5.2.1", "6.0.0", "6.0.1", "6.0.2", "6.0.3", "6.0.5", "6.2.1")
                .stream()
                .collect(Collectors.toCollection(
                    () -> new TreeSet<>(Comparator.comparing(ComparableVersion::new)))));
    }
    @Test
    public void testJoinWarVersions()
    {
        BeanResource extBean1 = new BeanResource("bean1", "default_context.xml",
            "org.alfresco.Dummy");
        BeanResource extBean11 = new BeanResource("bean1", "another_context.xml",
            "org.alfresco.Dummy");
        BeanResource warBean1 = new BeanResource("bean1", "default_war_context.xml",
            "org.alfresco.Dummy");

        Conflict c1 = new BeanOverwriteConflict(extBean1, warBean1, "6.0.2");
        Conflict c2 = new BeanOverwriteConflict(extBean11, warBean1, "6.0.2");
        Conflict c3 = new BeanOverwriteConflict(extBean1, warBean1, "6.0.1");
        Conflict c4 = new BeanOverwriteConflict(extBean1, warBean1, "6.0.5");
        Conflict c5 = new BeanOverwriteConflict(extBean1, warBean1, "6.0.0");
        Conflict c6 = new BeanOverwriteConflict(extBean11, warBean1, "5.2.0");
        Conflict c7 = new BeanOverwriteConflict(extBean1, warBean1, "6.2.1");
        Conflict c8 = new BeanOverwriteConflict(extBean1, warBean1, "5.2.4");
        Conflict c9 = new BeanOverwriteConflict(extBean1, warBean1, "5.2.1");
        Conflict c10 = new BeanOverwriteConflict(extBean1, warBean1, "6.0.3");

        Set<Conflict> conflicts = Set.of(c1, c2, c3, c4, c5, c6, c10);
        assertEquals("5.2.0,6.0.0-6.0.5", printer.joinWarVersions(conflicts));

        conflicts = Set.of(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10);
        assertEquals("5.2.0-6.2.1", printer.joinWarVersions(conflicts));

        conflicts = Set.of(c1, c2, c3, c4, c6, c7, c9, c10);
        assertEquals("5.2.0,5.2.1,6.0.1-6.2.1", printer.joinWarVersions(conflicts));

        conflicts = Set.of(c3, c4, c6, c7, c8, c9, c10);
        assertEquals("5.2.0-5.2.4,6.0.1,6.0.3-6.2.1", printer.joinWarVersions(conflicts));

        conflicts = Set.of(c6, c8, c9, c10);
        assertEquals("5.2.0-5.2.4,6.0.3", printer.joinWarVersions(conflicts));

        conflicts = Set.of(c6);
        assertEquals("5.2.0", printer.joinWarVersions(conflicts));

        conflicts = Set.of(c6, c9);
        assertEquals("5.2.0,5.2.1", printer.joinWarVersions(conflicts));

        conflicts = Set.of(c6, c8, c9);
        assertEquals("5.2.0-5.2.4", printer.joinWarVersions(conflicts));
    }

    @Test
    public void testJoinWarResourceDefiningObjs()
    {
        FileResource extFile1 = new FileResource("file1.txt", "file1.txt");
        FileResource warFile1 = new FileResource("file1.txt", "file1.txt");

        FileResource extFile2 = new FileResource("file2.txt", "file2.txt");
        FileResource warFile2 = new FileResource("file2.txt", "file2.txt");

        Conflict c1 = new FileOverwriteConflict(extFile1, warFile1, emptyMap(), "6.0.0.2");
        Conflict c2 = new FileOverwriteConflict(extFile2, warFile2, emptyMap(), "6.0.0.2");
        Conflict c3 = new FileOverwriteConflict(extFile1, warFile1, emptyMap(), "6.0.0.1");
        Conflict c4 = new FileOverwriteConflict(extFile1, warFile1, emptyMap(), "6.0.0.5");
        Conflict c5 = new FileOverwriteConflict(extFile2, warFile2, emptyMap(), "6.0.2");

        Set<Conflict> conflicts = Set.of(c1, c2, c3, c4, c5);

        assertEquals("file1.txt", joinWarResourceDefiningObjs("file1.txt", conflicts));
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

        assertEquals("another_context.xml,\ndefault_context.xml",
            joinExtensionDefiningObjs("bean2", conflicts));
    }
}
