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
package org.alfresco.extension_inspector.analyser.store;

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
            "6.2.0-6.2.1"
        ));
        assertNotNull(actualVersions);
        assertEquals(2, actualVersions.size());
        final Iterator<String> it = actualVersions.iterator();
        assertEquals("6.2.0", it.next());
        assertEquals("6.2.1", it.next());
    }

    @Test
    void testParse_singleRangeAndEmptyString()
    {
        final Collection<String> actualVersions = parser.parse(List.of(
            " ", "6.1.1-6.2.1"
        ));
        assertNotNull(actualVersions);
        assertEquals(3, actualVersions.size());
        final Iterator<String> it = actualVersions.iterator();
        assertEquals("6.1.1", it.next());
        assertEquals("6.2.0", it.next());
        assertEquals("6.2.1", it.next());
    }

    @Test
    void testParse_multipleRanges()
    {
        final Collection<String> actualVersions = parser.parse(List.of(
            " 6.0.0-6.1.1 ", "6.1.0-6.2.1 "
        ));
        assertNotNull(actualVersions);
        assertEquals(6, actualVersions.size());

        final Iterator<String> it = actualVersions.iterator();
        assertEquals("6.0.0", it.next());
        assertEquals("6.0.1", it.next());
        assertEquals("6.1.0", it.next());
        assertEquals("6.1.1", it.next());
        assertEquals("6.2.0", it.next());
        assertEquals("6.2.1", it.next());
    }

    @Test
    void testParse_singleValueAndSingleRange()
    {
        final Collection<String> actualVersions = parser.parse(List.of(
            "6.1.0 ", " 6.0.1-6.2.0"
        ));
        assertNotNull(actualVersions);
        assertEquals(4, actualVersions.size());

        final Iterator<String> it = actualVersions.iterator();
        assertEquals("6.0.1", it.next());

        assertEquals("6.1.0", it.next());
        assertEquals("6.1.1", it.next());
        assertEquals("6.2.0", it.next());
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