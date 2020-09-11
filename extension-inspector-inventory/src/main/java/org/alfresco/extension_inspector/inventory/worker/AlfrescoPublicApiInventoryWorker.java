/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.worker;

import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toCollection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

import org.alfresco.ampalyser.model.AlfrescoPublicApiResource;
import org.alfresco.ampalyser.model.Resource;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ObjectType;
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
    public Set<Resource> processInternal(ZipEntry zipEntry, byte[] data, String definingObject)
    {
        if (data == null)
        {
            return emptySet();
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
                Set<Resource> resources = new LinkedHashSet<>();
                resources.add(new AlfrescoPublicApiResource(jc.getClassName(), isDeprecated));
                resources.addAll(findImplicitAlfrescoPublicApis(jc));

                if (LOG.isTraceEnabled())
                {
                    resources
                        .iterator()
                        .forEachRemaining(
                            resource -> LOG.trace("AlfrescoPublicApi: " + resource.toString()));
                }
                return resources;
            }
        }
        catch (IOException e)
        {
            LOG.error("Class parsing error: ", e.getMessage());
        }
        return emptySet();
    }

    private static Set<AlfrescoPublicApiResource> findImplicitAlfrescoPublicApis(JavaClass javaClass)
    {
        // Add to the AlfrescoPublicApi resources all Alfresco classes used as methods arguments in given javaClass
        Set<AlfrescoPublicApiResource> methodArgs = stream(javaClass.getMethods())
            .flatMap(method -> 
                stream(method.getArgumentTypes())
                    .filter(t -> t instanceof ObjectType)
                    .map(t -> ((ObjectType) t).getClassName())
                    .filter(classname -> classname.startsWith("org.alfresco.") || classname
                        .startsWith("com.alfresco."))
                    .map(classname -> new AlfrescoPublicApiResource(classname, false, true)))
            .collect(toCollection(LinkedHashSet::new));

        // Add to the AlfrescoPublicApi resources all Alfresco Exceptions thrown by the given javaClass's methods
        Set<AlfrescoPublicApiResource> exceptions = stream(javaClass.getMethods())
            .map(Method::getExceptionTable)
            .filter(Objects::nonNull)
            .flatMap(exceptionTable ->  stream(exceptionTable.getExceptionNames())
                .filter(classname -> classname.startsWith("org.alfresco.") || 
                    classname.startsWith("com.alfresco."))
                .map(classname -> new AlfrescoPublicApiResource(classname, false, true)))
            .collect(toCollection(LinkedHashSet::new));

        LinkedHashSet<AlfrescoPublicApiResource> alfrescoPublicApiResources = 
            Stream.of(methodArgs, exceptions)
                .flatMap(Collection::stream)
                .collect(toCollection(LinkedHashSet::new));

        return unmodifiableSet(alfrescoPublicApiResources);
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
