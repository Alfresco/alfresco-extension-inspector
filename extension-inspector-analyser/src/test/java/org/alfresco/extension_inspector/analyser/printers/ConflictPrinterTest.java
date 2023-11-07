/*
 * Copyright 2023 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.extension_inspector.analyser.printers;

import static java.util.Collections.emptyMap;
import static org.alfresco.extension_inspector.analyser.printers.ConflictPrinter.joinExtensionDefiningObjs;
import static org.alfresco.extension_inspector.analyser.printers.ConflictPrinter.joinWarResourceDefiningObjs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.alfresco.extension_inspector.analyser.result.BeanOverwriteConflict;
import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.result.FileOverwriteConflict;
import org.alfresco.extension_inspector.analyser.store.WarInventoryReportStore;
import org.alfresco.extension_inspector.model.BeanResource;
import org.alfresco.extension_inspector.model.FileResource;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @BeforeEach
    public void setup()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testJoinWarVersions()
    {
        when(store.allKnownVersions()).thenReturn(
            Stream.of("5.2.0", "5.2.4", "5.2.1", "6.0.0", "6.0.1", "6.0.2", "6.0.3", "6.0.5", "6.2.1", "23.1.0", "7.4.0",
                    "7.4.1", "23.2.0", "7.4.2", "23.1.1", "23.3.0", "23.3.1")
                  .collect(Collectors.toCollection(
                   () -> new TreeSet<>(Comparator.comparing(ComparableVersion::new)))));

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
        Conflict c11 = new BeanOverwriteConflict(extBean1, warBean1, "7.4.2");
        Conflict c12 = new BeanOverwriteConflict(extBean1, warBean1, "7.4.1");
        Conflict c13 = new BeanOverwriteConflict(extBean1, warBean1, "23.1.0");
        Conflict c14 = new BeanOverwriteConflict(extBean1, warBean1, "7.4.0");
        Conflict c15 = new BeanOverwriteConflict(extBean1, warBean1, "23.2.0");
        Conflict c16 = new BeanOverwriteConflict(extBean1, warBean1, "23.3.0");
        Conflict c17 = new BeanOverwriteConflict(extBean1, warBean1, "23.3.1");

        Set<Conflict> conflicts = Set.of(c1, c2, c3, c4, c5, c6, c10);
        assertEquals("5.2.0;6.0.0-6.0.5", printer.joinWarVersions(conflicts));

        conflicts = Set.of(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10);
        assertEquals("5.2.0-6.2.1", printer.joinWarVersions(conflicts));

        conflicts = Set.of(c1, c2, c3, c4, c6, c7, c9, c10);
        assertEquals("5.2.0;5.2.1;6.0.1-6.2.1", printer.joinWarVersions(conflicts));

        conflicts = Set.of(c3, c4, c6, c7, c8, c9, c10);
        assertEquals("5.2.0-5.2.4;6.0.1;6.0.3-6.2.1", printer.joinWarVersions(conflicts));

        conflicts = Set.of(c6, c8, c9, c10);
        assertEquals("5.2.0-5.2.4;6.0.3", printer.joinWarVersions(conflicts));

        conflicts = Set.of(c6);
        assertEquals("5.2.0", printer.joinWarVersions(conflicts));

        conflicts = Set.of(c6, c9);
        assertEquals("5.2.0;5.2.1", printer.joinWarVersions(conflicts));

        conflicts = Set.of(c6, c8, c9);
        assertEquals("5.2.0-5.2.4", printer.joinWarVersions(conflicts));

        conflicts = Set.of(c15, c16, c17, c11, c12, c13, c14 );
        assertEquals("7.4.0-23.1.0;23.2.0-23.3.1", printer.joinWarVersions(conflicts));
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

        assertEquals("another_context.xml\n\ndefault_context.xml",
            joinExtensionDefiningObjs("bean2", conflicts));
    }
}
