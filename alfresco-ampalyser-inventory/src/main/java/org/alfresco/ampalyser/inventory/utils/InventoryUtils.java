/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.utils;

import java.util.List;
import java.util.zip.ZipEntry;

import org.alfresco.ampalyser.inventory.model.Resource;

public class InventoryUtils
{
    public static final String WEB_INF_LIB = "WEB-INF/lib/";

    public static boolean isFromJar(ZipEntry entry, String definingObject)
    {
        return !isJar(entry) && definingObject != null && definingObject.startsWith(WEB_INF_LIB);
    }

    public static boolean isJar(ZipEntry entry)
    {
        return entry != null &&
            entry.getName().startsWith(WEB_INF_LIB) &&
            entry.getName().endsWith(".jar");
    }

    public static List<Resource> mergeLists(List<Resource> v1, List<Resource> v2)
    {
        if (v1 != null && v2 != null && !v2.isEmpty())
        {
            v1.addAll(v2);
        }
        return v1;
    }
}
