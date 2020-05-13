/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.worker;

import org.alfresco.ampalyser.inventory.EntryProcessor;
import org.alfresco.ampalyser.inventory.model.AlfrescoPublicApiResource;
import org.alfresco.ampalyser.inventory.model.Resource;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;

@Component
public class AlfrescoPublicApiInventoryWorker extends AbstractInventoryWorker
{
    private static final Logger LOG = LoggerFactory.getLogger(AlfrescoPublicApiInventoryWorker.class);

    public AlfrescoPublicApiInventoryWorker(EntryProcessor processor)
    {
        processor.attach(this);
    }

    @Override
    public List<Resource> processInternal(ZipEntry zipEntry, byte[] data, String definingObject)
    {
        List<Resource> publicApiClasses = new ArrayList<>();
        if (data == null)
        {
            return publicApiClasses;
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
                    if (ae.getAnnotationType().equals("Lorg/alfresco/api/AlfrescoPublicApi;"))
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
                AlfrescoPublicApiResource resource = new AlfrescoPublicApiResource();
                resource.setName(jc.getClassName());
                resource.setDeprecated(isDeprecated);

                publicApiClasses.add(resource);

                LOG.debug("AlfrescoPublicApi: " + resource.toString());
            }
        }
        catch (IOException e)
        {
            LOG.error("Class parsing error: ", e.getMessage());
        }
        return publicApiClasses;
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
               entry.getName().startsWith("org/alfresco");
    }
}
