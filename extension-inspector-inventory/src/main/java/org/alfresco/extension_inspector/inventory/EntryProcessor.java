/*
 * Copyright 2021 Alfresco Software, Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import static org.alfresco.extension_inspector.commons.InventoryUtils.isJar;
import static org.alfresco.extension_inspector.model.Resource.Type.FILE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.alfresco.extension_inspector.commons.InventoryUtils;
import org.alfresco.extension_inspector.inventory.worker.InventoryWorker;
import org.alfresco.extension_inspector.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntryProcessor
{
    @Autowired
    private List<InventoryWorker> inventoryWorkers;

    public Map<Resource.Type, Set<Resource>> processWarEntry(ZipEntry warEntry, ZipInputStream zis)
        throws IOException
    {
        if (warEntry == null || zis == null)
        {
            throw new IllegalArgumentException("Arguments should not be null.");
        }

        final Map<Resource.Type, Set<Resource>> extractedResources = new EnumMap<>(Resource.Type.class);
        // add modifiable sets for each inventoryWorker type
        // to be able to merge results later
        inventoryWorkers.forEach(inventoryWorker -> extractedResources
            .put(inventoryWorker.getType(), new LinkedHashSet<>()));

        byte[] data = InventoryUtils.extract(zis);

        if (!isFileToBeIgnored(warEntry.getName()))
        {
            processEntry(warEntry, data, warEntry.getName(), extractedResources);
        }

        if (isJar(warEntry.getName()))
        {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ZipInputStream libZis = new ZipInputStream(bis);
            ZipEntry libZe = libZis.getNextEntry();
            while (libZe != null)
            {
                if (!(libZe.isDirectory() || isFileToBeIgnored(libZe.getName())))
                {
                    byte[] libData = InventoryUtils.extract(libZis);
                    processEntryWithinJar(libZe, libData, warEntry.getName(), extractedResources);
                }
                libZis.closeEntry();
                libZe = libZis.getNextEntry();
            }
        }
        return extractedResources;
    }

    /**
     * Evaluates whether or not is to be ignored at the extension applying process
     *
     * @param fileName
     * @return
     */
    private static boolean isFileToBeIgnored(String fileName)
    {
        return fileName.startsWith("META-INF/") ||
            fileName.equals("module-info.class") ||
            fileName.equalsIgnoreCase("license.txt") ||
            fileName.equalsIgnoreCase("notice.txt");
    }

    private void processEntry(ZipEntry entry, byte[] data, String definingObject,
        Map<Resource.Type, Set<Resource>> resources)
    {
        inventoryWorkers.forEach(inventoryWorker -> resources.merge(inventoryWorker.getType(),
            inventoryWorker.processZipEntry(entry, data, definingObject),
            InventoryUtils::mergeCollections));
    }

    private void processEntryWithinJar(ZipEntry entry, byte[] data, String definingObject,
        Map<Resource.Type, Set<Resource>> resources)
    {
        inventoryWorkers.stream()
            .filter(iw -> iw.getType() != FILE)
            .forEach(inventoryWorker -> resources.merge(inventoryWorker.getType(),
                inventoryWorker.processZipEntry(entry, data, definingObject),
                InventoryUtils::mergeCollections));
    }
}
