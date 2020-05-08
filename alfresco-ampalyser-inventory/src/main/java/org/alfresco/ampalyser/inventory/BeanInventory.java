/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory;

import java.util.zip.ZipEntry;

public class BeanInventory implements Inventory
{
    public BeanInventory(ResourceProcessor processor)
    {
        processor.attach(this);
    }
    @Override
    public void processResource(ZipEntry resource)
    {

    }
}
