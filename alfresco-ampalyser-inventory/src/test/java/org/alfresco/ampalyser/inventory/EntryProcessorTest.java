package org.alfresco.ampalyser.inventory;

import java.io.IOException;
import java.util.Set;

import org.alfresco.ampalyser.inventory.worker.AlfrescoPublicApiInventoryWorker;
import org.alfresco.ampalyser.inventory.worker.BeanInventoryWorker;
import org.alfresco.ampalyser.inventory.worker.ClasspathElementInventoryWorker;
import org.alfresco.ampalyser.inventory.worker.FileInventoryWorker;
import org.alfresco.ampalyser.inventory.worker.InventoryWorker;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class EntryProcessorTest
{
    private static final Logger logger = LoggerFactory.getLogger(EntryProcessorTest.class);

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

        assertEquals(expectedWorkers.size(), workers.size(), "Registered worker number doesn't match");
        expectedWorkers.forEach(ew -> assertTrue(
            workers.stream().anyMatch(w -> ew.isAssignableFrom(w.getClass())),
                "No worker of type " + ew.getSimpleName() + " found"
        ));
    }

    @Test
    public void testProcessNullWarEntryShouldThrowException() throws IOException
    {
        try
        {
            entryProcessor.processWarEntry(null, null);
            fail("Null entry processing should have failed.");
        }
        catch(IllegalArgumentException e)
        {
            logger.info("Expected exception when processing null entry.");
        }
    }
}
