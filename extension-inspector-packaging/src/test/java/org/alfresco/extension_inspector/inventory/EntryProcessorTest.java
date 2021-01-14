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
package org.alfresco.extension_inspector.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.alfresco.extension_inspector.inventory.worker.AlfrescoPublicApiInventoryWorker;
import org.alfresco.extension_inspector.inventory.worker.BeanInventoryWorker;
import org.alfresco.extension_inspector.inventory.worker.ClasspathElementInventoryWorker;
import org.alfresco.extension_inspector.inventory.worker.FileInventoryWorker;
import org.alfresco.extension_inspector.inventory.worker.InventoryWorker;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class EntryProcessorTest
{
    private static final Logger logger = LoggerFactory.getLogger(EntryProcessorTest.class);

    @Autowired EntryProcessor entryProcessor;

    @Test
    public void testProcessorInjection()
    {
        final List<InventoryWorker> workers = (List<InventoryWorker>) ReflectionTestUtils
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
