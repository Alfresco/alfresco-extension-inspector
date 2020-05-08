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
 * @author Lucian Tuca
 * created on 08/05/2020
 */
public abstract  class AbstractInventoryWorker implements InventoryWorker
{
    @Override
    public List<Resource> processZipEntry(ZipEntry entry)
    {
        if (this.canProcessEntry(entry))
        {
            return processInternal(entry);
        }
        return emptyList();
    }

    public abstract List<Resource> processInternal(ZipEntry entry);
}
