/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;

public class ResourceProcessor
{
    private List<Inventory> inventories = new ArrayList<>();

    public void attach(Inventory inventory)
    {
        inventories.add(inventory);
    }

    public void processResource(ZipEntry resource)
    {
        inventories.stream().forEach(inventory -> inventory.processResource(resource));
    }
}
