/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.store;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AlfrescoTargetVersionParserTest
{
    @Autowired
    private WarInventoryReportStore inventoryStore;

    @Autowired
    private AlfrescoTargetVersionParser parser;

    @Test
    void testParse_null()
    {
        // the case where the "--target=..." option is not specified at all
        final Collection<String> actualVersions = parser.parse(null);

        assertNotNull(actualVersions);
        assertEquals(inventoryStore.allKnownVersions().size(), actualVersions.size());
        assertTrue(actualVersions.containsAll(inventoryStore.allKnownVersions()));
    }

    @Test
    void testParse_empty()
    {
        // the case where the "--target" option is specified, but without any value
        final Collection<String> actualVersions = parser.parse(emptyList());
        assertNotNull(actualVersions);
        assertTrue(actualVersions.isEmpty());
    }

    @Test
    void testParse_singleVersion()
    {
        final Collection<String> actualVersions = parser.parse(List.of(
            "6.0.0"
        ));
        assertNotNull(actualVersions);
        assertEquals(1, actualVersions.size());
        assertEquals("6.0.0", actualVersions.iterator().next());
    }

    @Test
    void testParse_singleVersionAndEmptyString()
    {
        final Collection<String> actualVersions = parser.parse(List.of(
            "6.0.0", ""
        ));
        assertNotNull(actualVersions);
        assertEquals(1, actualVersions.size());
        assertEquals("6.0.0", actualVersions.iterator().next());
    }

    @Test
    void testParse_multipleValues()
    {
        final Collection<String> actualVersions = parser.parse(List.of(
            "6.0.0", "6.2.1"
        ));
        assertNotNull(actualVersions);
        assertEquals(2, actualVersions.size());
        final Iterator<String> it = actualVersions.iterator();
        assertEquals("6.0.0", it.next());
        assertEquals("6.2.1", it.next());
    }

    @Test
    void testParse_singleRange()
    {
        final Collection<String> actualVersions = parser.parse(List.of(
            "6.2.0.1-6.2.0.3"
        ));
        assertNotNull(actualVersions);
        assertEquals(3, actualVersions.size());
        final Iterator<String> it = actualVersions.iterator();
        assertEquals("6.2.0.1", it.next());
        assertEquals("6.2.0.2", it.next());
        assertEquals("6.2.0.3", it.next());
    }

    @Test
    void testParse_singleRangeAndEmptyString()
    {
        final Collection<String> actualVersions = parser.parse(List.of(
            " ", "6.2.0.1-6.2.0.3"
        ));
        assertNotNull(actualVersions);
        assertEquals(3, actualVersions.size());
        final Iterator<String> it = actualVersions.iterator();
        assertEquals("6.2.0.1", it.next());
        assertEquals("6.2.0.2", it.next());
        assertEquals("6.2.0.3", it.next());
    }

    @Test
    void testParse_multipleRanges()
    {
        final Collection<String> actualVersions = parser.parse(List.of(
            " 6.1.0.4-6.1.0.6 ", "6.2.0.1-6.2.0.3 "
        ));
        assertNotNull(actualVersions);
        assertEquals(6, actualVersions.size());

        final Iterator<String> it = actualVersions.iterator();
        assertEquals("6.1.0.4", it.next());
        assertEquals("6.1.0.5", it.next());
        assertEquals("6.1.0.6", it.next());

        assertEquals("6.2.0.1", it.next());
        assertEquals("6.2.0.2", it.next());
        assertEquals("6.2.0.3", it.next());
    }

    @Test
    void testParse_singleValueAndSingleRange()
    {
        final Collection<String> actualVersions = parser.parse(List.of(
            "6.1.0.4 ", " 6.2.0.1-6.2.0.3"
        ));
        assertNotNull(actualVersions);
        assertEquals(4, actualVersions.size());

        final Iterator<String> it = actualVersions.iterator();
        assertEquals("6.1.0.4", it.next());

        assertEquals("6.2.0.1", it.next());
        assertEquals("6.2.0.2", it.next());
        assertEquals("6.2.0.3", it.next());
    }

    @Test
    void testParse_unknownVersion()
    {
        final Collection<String> actualVersions = parser.parse(List.of(
            "2.0.5"
        ));
        assertNotNull(actualVersions);
        assertTrue(actualVersions.isEmpty());
    }

    @Test
    void testParse_bothKnownAndUnknownVersion()
    {
        final Collection<String> actualVersions = parser.parse(List.of(
            "2.0.5", "6.2.1"
        ));
        assertNotNull(actualVersions);
        assertEquals(1, actualVersions.size());
        assertEquals("6.2.1", actualVersions.iterator().next());
    }
}