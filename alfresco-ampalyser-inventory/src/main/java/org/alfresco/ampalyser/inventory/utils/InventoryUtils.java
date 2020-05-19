/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.utils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.alfresco.ampalyser.inventory.model.InventoryReport;
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

    public static byte[] extract(ZipInputStream zis) throws IOException
    {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len;
        while ((len = zis.read(buffer)) > 0)
        {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * @param fileEntry a zip entry for the manifest.mf file from a zip
     * @param zis input stream for reading the file
     * @return a map with the identified versions from the manifest.mf file
     * @throws IOException
     */
    public static Map<String, String> parseManifestForVersion(ZipEntry fileEntry, ZipInputStream zis) throws IOException
    {
        if (fileEntry == null || !fileEntry.getName().endsWith("MANIFEST.MF"))
        {
            return null;
        }
        byte[] data = extract(zis);
        Manifest manifest = new Manifest(new ByteArrayInputStream(data));

        Map<String, String> versions = new HashMap<>();
        Attributes attributes = manifest.getMainAttributes();
        versions.put(InventoryReport.IMPLEMENTATION_VERSION, attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION));
        versions.put(InventoryReport.SPECIFICATION_VERSION, attributes.getValue(Attributes.Name.SPECIFICATION_VERSION));
        return versions;
    }
}
