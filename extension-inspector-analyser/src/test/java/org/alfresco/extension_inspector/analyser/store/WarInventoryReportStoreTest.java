/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.store;

import static org.alfresco.ampalyser.model.Resource.Type.ALFRESCO_PUBLIC_API;
import static org.alfresco.ampalyser.model.Resource.Type.BEAN;
import static org.alfresco.ampalyser.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.alfresco.ampalyser.model.Resource.Type.FILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import org.alfresco.ampalyser.model.InventoryReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"inventory-report-resource-pattern=classpath:bundled-inventories-test/*.json"})
class WarInventoryReportStoreTest
{
    @Autowired
    private WarInventoryReportStore inventoryStore;

    @Test
    void retrieve()
    {
        final InventoryReport report = inventoryStore.retrieve("6.0.0");
        assertNotNull(report);
        assertNotNull(report.getSchemaVersion());
        assertEquals("6.0.0", report.getAlfrescoVersion());
        assertNotNull(report.getResources());
        assertTrue(report.getResources().containsKey(FILE));
        assertFalse(report.getResources().get(FILE).isEmpty());
        assertTrue(report.getResources().containsKey(CLASSPATH_ELEMENT));
        assertFalse(report.getResources().get(CLASSPATH_ELEMENT).isEmpty());
        assertTrue(report.getResources().containsKey(BEAN));
        assertFalse(report.getResources().get(BEAN).isEmpty());
        assertTrue(report.getResources().containsKey(ALFRESCO_PUBLIC_API));
        assertFalse(report.getResources().get(ALFRESCO_PUBLIC_API).isEmpty());
    }

    @Test
    void allKnownVersions()
    {
        final Collection<String> actualVersions = inventoryStore.allKnownVersions();
        assertEquals(3, actualVersions.size());

        final Iterator<String> iterator = actualVersions.iterator();
        assertEquals("6.0.0", iterator.next());
        assertEquals("6.1.0", iterator.next());
        assertEquals("6.1.1", iterator.next());
    }

    @Test
    void knownVersions1()
    {
        final Collection<String> actualVersions = inventoryStore.knownVersions("6.0.0-6.1.1");
        assertEquals(3, actualVersions.size());

        final Iterator<String> iterator = actualVersions.iterator();
        assertEquals("6.0.0", iterator.next());
        assertEquals("6.1.0", iterator.next());
        assertEquals("6.1.1", iterator.next());
    }

    @Test
    void knownVersions2()
    {
        final Collection<String> actualVersions = inventoryStore.knownVersions("6.0.0-6.1.0");
        assertEquals(2, actualVersions.size());

        final Iterator<String> iterator = actualVersions.iterator();
        assertEquals("6.0.0", iterator.next());
        assertEquals("6.1.0", iterator.next());
    }

    @Test
    void knownVersions3()
    {
        final Collection<String> actualVersions = inventoryStore.knownVersions("6.1.0-8.0.0");
        assertEquals(2, actualVersions.size());

        final Iterator<String> iterator = actualVersions.iterator();
        assertEquals("6.1.0", iterator.next());
        assertEquals("6.1.1", iterator.next());
    }

    @Test
    void knownVersions4()
    {
        final Collection<String> actualVersions = inventoryStore.knownVersions("6.1.0-6.0.0");
        assertEquals(0, actualVersions.size());
    }

    @Test
    void knownVersions5()
    {
        final Collection<String> actualVersions = inventoryStore.knownVersions("6.1-8.0");
        assertEquals(2, actualVersions.size());

        final Iterator<String> iterator = actualVersions.iterator();
        assertEquals("6.1.0", iterator.next());
        assertEquals("6.1.1", iterator.next());
    }

    @Test
    void knownVersions6()
    {
        final Collection<String> actualVersions = inventoryStore.knownVersions("5-8");
        assertEquals(3, actualVersions.size());

        final Iterator<String> iterator = actualVersions.iterator();
        assertEquals("6.0.0", iterator.next());
        assertEquals("6.1.0", iterator.next());
        assertEquals("6.1.1", iterator.next());
    }

    @Test
    void isKnown()
    {
        assertTrue(inventoryStore.isKnown("6.0.0"));
        assertFalse(inventoryStore.isKnown("3.0.0"));
    }
}