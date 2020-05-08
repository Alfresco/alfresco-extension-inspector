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
import org.springframework.stereotype.Component;

@Component
public class ResourceProcessor
{
    private List<Inventory> inventories = new ArrayList<>();

    public void attach(Inventory inventory)
    {
        inventories.add(inventory);
    }

    public Map<Resource.Type, List<Resource>> processResource(ZipEntry resource)
    {
        Map<Resource.Type, List<Resource>> resources = new HashMap<>();
        inventories.stream()
            .forEach(inventory ->
                resources.put(inventory.getType(), inventory.processResource(resource)));
        return resources;
    }
}
