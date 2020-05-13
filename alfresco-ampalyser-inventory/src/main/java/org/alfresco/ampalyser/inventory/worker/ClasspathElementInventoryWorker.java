/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.worker;

import static java.util.List.of;

import java.util.List;
import java.util.zip.ZipEntry;

import org.alfresco.ampalyser.inventory.EntryProcessor;
import org.alfresco.ampalyser.inventory.model.ClasspathElementResource;
import org.alfresco.ampalyser.inventory.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class ClasspathElementInventoryWorker extends AbstractInventoryWorker
{
    public static final String WEB_INF_CLASSES = "WEB-INF/classes/";
    public static final String WEB_INF_LIB = "WEB-INF/lib/";

    public ClasspathElementInventoryWorker(EntryProcessor processor)
    {
        processor.attach(this);
    }

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
        return !(entry == null || definingObject == null) && !entry.isDirectory() &&
            (entry.getName().startsWith(WEB_INF_CLASSES) || isFromJar(entry, definingObject));
    }

    private List<Resource> processInternal(ZipEntry zipEntry, String definingObject)
    {
        String resourceName = zipEntry.getName();
        if (resourceName.startsWith(WEB_INF_CLASSES))
        {
            resourceName = resourceName.substring(WEB_INF_CLASSES.length());
        }
        return List.of(new ClasspathElementResource(resourceName, definingObject));
    }

    private boolean isFromJar(ZipEntry entry, String definingObject)
    {
        return !isJar(entry) && definingObject != null && definingObject.startsWith(WEB_INF_LIB);
    }

    private boolean isJar(ZipEntry entry)
    {
        return entry != null &&
            entry.getName().startsWith(WEB_INF_LIB) &&
            entry.getName().endsWith(".jar");
    }
}
