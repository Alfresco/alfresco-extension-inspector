/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.utils;

import java.util.List;

import org.alfresco.ampalyser.inventory.model.Resource;

public class InventoryUtils
{
    public static List<Resource> mergeLists(List<Resource> v1, List<Resource> v2)
    {
        if (v1 != null && v2 != null && !v2.isEmpty())
        {
            v1.addAll(v2);
        }
        return v1;
    }
}
