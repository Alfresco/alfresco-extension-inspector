/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory;

import static org.alfresco.ampalyser.inventory.utils.InventoryUtils.isJar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.alfresco.ampalyser.inventory.model.Resource;
import org.alfresco.ampalyser.inventory.model.Resource.Type;
import org.alfresco.ampalyser.inventory.utils.InventoryUtils;
import org.alfresco.ampalyser.inventory.worker.InventoryWorker;
import org.springframework.stereotype.Component;

@Component
public class EntryProcessor
{
    private Set<InventoryWorker> inventoryWorkers = new LinkedHashSet<>();

    public void attach(InventoryWorker inventoryWorker)
    {
        inventoryWorkers.add(inventoryWorker);
    }

    public Map<Type, List<Resource>> processWarEntry(ZipEntry warEntry, ZipInputStream zis)
        throws IOException
    {
        if (warEntry == null || zis == null)
        {
            throw new IllegalArgumentException("Arguments should not be null.");
        }
        Map<Resource.Type, List<Resource>> extractedResources = new HashMap<>();
        // add modifiable lists for each inventoryWorker type
        // to be able to merge results later
        inventoryWorkers.forEach(inventoryWorker -> extractedResources
            .put(inventoryWorker.getType(), new ArrayList<>()));

        byte[] data = extract(zis);
        processEntry(warEntry, data, warEntry.getName(), extractedResources);

        if (isJar(warEntry))
        {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ZipInputStream libZis = new ZipInputStream(bis);
            ZipEntry libZe = libZis.getNextEntry();
            while (libZe != null)
            {
                if (!(libZe.isDirectory() ||
                      libZe.getName().startsWith("META-INF/") ||
                      libZe.getName().equals("module-info.class") ||
                      libZe.getName().equalsIgnoreCase("license.txt") ||
                      libZe.getName().equalsIgnoreCase("notice.txt")))
                {
                    byte[] libData = extract(libZis);
                    processEntry(libZe, libData, warEntry.getName(), extractedResources);
                }
                libZis.closeEntry();
                libZe = libZis.getNextEntry();
            }
        }
        return extractedResources;
    }

    private void processEntry(ZipEntry entry, byte[] data, String definingObject,
        Map<Resource.Type, List<Resource>> resources)
    {
        inventoryWorkers.forEach(inventoryWorker -> resources.merge(inventoryWorker.getType(),
            inventoryWorker.processZipEntry(entry, data, definingObject),
            InventoryUtils::mergeLists));
    }

    private static byte[] extract(ZipInputStream zis) throws IOException
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
}
