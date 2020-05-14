/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.worker;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.zip.ZipEntry;

import org.alfresco.ampalyser.inventory.model.Resource;

/**
 * @author Denis Ungureanu
 * @author Lucian Tuca
 */
public interface InventoryWorker
{
    default List<Resource> processZipEntry(ZipEntry entry, byte[] data, String definingObject)
    {
        if (this.canProcessEntry(entry, definingObject))
        {
            return processInternal(entry, data, definingObject);
        }
        return emptyList();
    }

    List<Resource> processInternal(ZipEntry entry, byte[] data, String definingObject);

    Resource.Type getType();

    boolean canProcessEntry(ZipEntry entry, String definingObject);
}
