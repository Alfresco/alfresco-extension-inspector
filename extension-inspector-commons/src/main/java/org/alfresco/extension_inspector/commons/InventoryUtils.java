/*
 * Copyright 2021 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfresco.extension_inspector.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.alfresco.extension_inspector.model.InventoryReport;
import org.alfresco.extension_inspector.model.Resource;

public class InventoryUtils
{
    public static boolean isFromExtension(ZipEntry entry)
    {
        return entry != null && 
            (entry.getName().endsWith(".class") ||
                // when the extension is an AMP
                entry.getName().startsWith("config/") || 
                // when the extension is a JAR
                entry.getName().startsWith("alfresco/"));
    }

    public static boolean isFromJar(ZipEntry entry, String definingObject)
    {
        return entry != null && !isJar(entry.getName()) && isJar(definingObject);
    }

    public static boolean isJar(String path)
    {
        return path != null && path.endsWith(".jar");
    }

    public static <T extends Collection<Resource>> T mergeCollections(T v1, Collection<Resource> v2)
    {
        if (v1 != null && v2 != null && !v2.isEmpty())
        {
            v1.addAll(v2);
        }
        return v1;
    }

    public static byte[] extract(InputStream zis) throws IOException
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
