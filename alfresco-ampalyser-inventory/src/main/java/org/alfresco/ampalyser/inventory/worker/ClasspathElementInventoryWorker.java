/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.worker;

import static java.util.Collections.singletonList;
import static org.alfresco.ampalyser.commons.InventoryUtils.isFromExtension;
import static org.alfresco.ampalyser.commons.InventoryUtils.isFromJar;

import java.util.List;
import java.util.zip.ZipEntry;

import org.alfresco.ampalyser.model.ClasspathElementResource;
import org.alfresco.ampalyser.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class ClasspathElementInventoryWorker implements InventoryWorker
{
    public static final String WEB_INF_CLASSES = "WEB-INF/classes/";

    @Override
    public List<Resource> processInternal(ZipEntry zipEntry, byte[] data, String definingObject)
    {
        return processInternal(zipEntry, definingObject);
    }

    @Override
    public Resource.Type getType()
    {
        return Resource.Type.CLASSPATH_ELEMENT;
    }

    @Override
    public boolean canProcessEntry(ZipEntry entry, String definingObject)
    {
        return !(entry == null || definingObject == null) &&
            !entry.isDirectory() &&
            (entry.getName().startsWith(WEB_INF_CLASSES)
                || isFromExtension(entry)
                || isFromJar(entry, definingObject));
    }

    private List<Resource> processInternal(ZipEntry zipEntry, String definingObject)
    {
        String resourceName = zipEntry.getName();
        if (resourceName.startsWith(WEB_INF_CLASSES))
        {
            resourceName = resourceName.substring(WEB_INF_CLASSES.length());
        }
        return singletonList(new ClasspathElementResource("/" + resourceName, "/" + definingObject));
    }
}
