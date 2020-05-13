package org.alfresco.ampalyser.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.alfresco.ampalyser.inventory.worker.AlfrescoPublicApiInventoryWorker;
import org.alfresco.ampalyser.inventory.worker.BeanInventoryWorker;
import org.alfresco.ampalyser.inventory.worker.ClasspathElementInventoryWorker;
import org.alfresco.ampalyser.inventory.worker.FileInventoryWorker;
import org.alfresco.ampalyser.inventory.worker.InventoryWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class EntryProcessorTest
{
    @Autowired EntryProcessor entryProcessor;

    @Test
    public void testProcessorInjection()
    {
        final Set<InventoryWorker> workers = (Set<InventoryWorker>) ReflectionTestUtils
            .getField(entryProcessor, "inventoryWorkers");

        assertNotNull(workers);

        final Set<Class<?>> expectedWorkers = Set.of(
            FileInventoryWorker.class,
            ClasspathElementInventoryWorker.class,
            BeanInventoryWorker.class,
            AlfrescoPublicApiInventoryWorker.class);

        assertEquals("Registerd worker number doesn't match", expectedWorkers.size(), workers.size());
        expectedWorkers.forEach(ew -> assertTrue(
            "No worker of type " + ew.getSimpleName() + " found",
            workers.stream().anyMatch(w -> ew.isAssignableFrom(w.getClass()))
        ));
    }
}
