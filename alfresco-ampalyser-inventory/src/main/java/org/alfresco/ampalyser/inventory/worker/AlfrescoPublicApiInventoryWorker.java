/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.worker;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;

import org.alfresco.ampalyser.model.AlfrescoPublicApiResource;
import org.alfresco.ampalyser.model.Resource;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AlfrescoPublicApiInventoryWorker implements InventoryWorker
{
    private static final Logger LOG = LoggerFactory.getLogger(AlfrescoPublicApiInventoryWorker.class);
    private static final String ALFRESCO_SOURCE = "org/alfresco";
    private static final String ALFRESCO_PUBLIC_API_ANNOTATION = "Lorg/alfresco/api/AlfrescoPublicApi;";

    @Override
    public List<Resource> processInternal(ZipEntry zipEntry, byte[] data, String definingObject)
    {
        if (data == null)
        {
            return emptyList();
        }
        ClassParser cp = new ClassParser(new ByteArrayInputStream(data), null);
        try
        {
            JavaClass jc = cp.parse();
            AnnotationEntry[] aes = jc.getAnnotationEntries();
            boolean isAlfrescoPublicApi = false;
            boolean isDeprecated = false;
            if (aes != null && aes.length > 0)
            {
                for (AnnotationEntry ae : aes)
                {
                    if (ae.getAnnotationType().equals(getPublicAnnotationType()))
                    {
                        isAlfrescoPublicApi = true;
                    }
                    if (ae.getAnnotationType().equals("Ljava/lang/Deprecated;"))
                    {
                        isDeprecated = true;
                    }
                }
            }
            if (isAlfrescoPublicApi)
            {
                AlfrescoPublicApiResource resource = new AlfrescoPublicApiResource(jc.getClassName(), isDeprecated);

                if (LOG.isTraceEnabled())
                {
                    LOG.trace("AlfrescoPublicApi: " + resource.toString());
                }
                return singletonList(resource);
            }
        }
        catch (IOException e)
        {
            LOG.error("Class parsing error: ", e.getMessage());
        }
        return emptyList();
    }

    public Resource.Type getType()
    {
        return Resource.Type.ALFRESCO_PUBLIC_API;
    }

    @Override
    public boolean canProcessEntry(ZipEntry entry, String definingObject)
    {
        //Only process classes from alfresco code
        return entry != null && entry.getName() != null &&
               entry.getName().endsWith(".class") &&
               entry.getName().startsWith(ALFRESCO_SOURCE);
    }

    protected String getPublicAnnotationType()
    {
        return ALFRESCO_PUBLIC_API_ANNOTATION;
    }
}
