/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.inventory.model;

import static org.alfresco.ampalyser.inventory.model.Resource.Type.ALFRESCO_PUBLIC_API;
import static org.alfresco.ampalyser.inventory.model.Resource.Type.BEAN;
import static org.alfresco.ampalyser.inventory.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.alfresco.ampalyser.inventory.model.Resource.Type.FILE;

import java.io.Serializable;

/**
 * @author Lucian Tuca
 * created on 07/05/2020
 */
public interface Resource extends Serializable
{
    enum Type
    {
        FILE, BEAN, ALFRESCO_PUBLIC_API, CLASSPATH_ELEMENT
    }

    Type getType();

    String getDefiningObject();

    void setDefiningObject(String definingObject);

    String getId();

    void setId(String id);

    static boolean isFile(final Resource r)
    {
        return r != null && r.getType() == FILE;
    }

    static boolean isBean(final Resource r)
    {
        return r != null && r.getType() == BEAN;
    }

    static boolean isPublicApi(final Resource r)
    {
        return r != null && r.getType() == ALFRESCO_PUBLIC_API;
    }

    static boolean isClasspathElement(final Resource r)
    {
        return r != null && r.getType() == CLASSPATH_ELEMENT;
    }
}
