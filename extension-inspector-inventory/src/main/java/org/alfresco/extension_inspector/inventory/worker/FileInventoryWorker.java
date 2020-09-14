/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.extension_inspector.inventory.worker;

import static java.util.Collections.singleton;
import static org.alfresco.extension_inspector.commons.InventoryUtils.isFromJar;

import java.util.Set;
import java.util.zip.ZipEntry;

import org.alfresco.extension_inspector.model.FileResource;
import org.alfresco.extension_inspector.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class FileInventoryWorker implements InventoryWorker
{
    @Override
    public Set<Resource> processInternal(ZipEntry zipEntry, byte[] data, String definingObject)
    {
        return singleton(new FileResource("/" + zipEntry.getName(), "/" + definingObject));
    }

    @Override
    public Resource.Type getType()
    {
        return Resource.Type.FILE;
    }

    @Override
    public boolean canProcessEntry(ZipEntry entry, String definingObject)
    {
        return !(entry == null || entry.isDirectory() || isFromJar(entry, definingObject));
    }
}
