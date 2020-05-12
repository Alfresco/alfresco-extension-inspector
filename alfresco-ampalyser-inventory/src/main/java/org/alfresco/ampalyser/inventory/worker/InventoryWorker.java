/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.worker;

import java.util.List;
import java.util.zip.ZipEntry;

import org.alfresco.ampalyser.inventory.model.Resource;

public interface InventoryWorker
{
    List<Resource> processZipEntry(ZipEntry entry, byte[] data, String definingObject);

    Resource.Type getType();

    boolean canProcessEntry(ZipEntry entry);
}
