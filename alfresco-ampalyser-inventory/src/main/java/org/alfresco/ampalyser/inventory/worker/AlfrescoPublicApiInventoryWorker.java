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

import org.alfresco.ampalyser.inventory.EntryProcessor;
import org.alfresco.ampalyser.inventory.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class AlfrescoPublicApiInventoryWorker extends AbstractInventoryWorker
{
    public AlfrescoPublicApiInventoryWorker(EntryProcessor processor)
    {
        processor.attach(this);
    }

    @Override
    public List<Resource> processInternal(ZipEntry zipEntry, byte[] data, String definingObject)
    {
        //TODO add logic
        return emptyList();
    }

    public Resource.Type getType()
    {
        return Resource.Type.ALFRESCO_PUBLIC_API;
    }

    @Override
    public boolean canProcessEntry(ZipEntry entry, String definingObject)
    {
        return false;
    }
}
