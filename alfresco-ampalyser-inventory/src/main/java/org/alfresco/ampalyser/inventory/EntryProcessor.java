/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;

import org.alfresco.ampalyser.inventory.model.Resource;
import org.alfresco.ampalyser.inventory.worker.InventoryWorker;
import org.springframework.stereotype.Component;

@Component
public class EntryProcessor
{
    private List<InventoryWorker> inventoryWorkers = new ArrayList<>();

    public void attach(InventoryWorker inventoryWorker)
    {
        inventoryWorkers.add(inventoryWorker);
    }

    public Map<Resource.Type, List<Resource>> processWarEntry(ZipEntry warEntry)
    {
        Map<Resource.Type, List<Resource>> resources = new HashMap<>();
        inventoryWorkers
            .forEach(inventoryWorker ->
                resources.put(inventoryWorker.getType(), inventoryWorker.processZipEntry(warEntry)));
        return resources;
    }
}
