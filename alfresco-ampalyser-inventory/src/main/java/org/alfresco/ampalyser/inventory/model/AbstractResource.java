/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.model;

/**
 * @author Lucian Tuca
 * created on 07/05/2020
 */
public abstract class AbstractResource implements Resource
{
    private Resource.Type type;

    public Resource.Type getType()
    {
        return type;
    }

    public void setType(Resource.Type type)
    {
        this.type = type;
    }

    @Override
    public boolean isFile()
    {
        return type == Type.FILE;
    }

    @Override
    public boolean isBean()
    {
        return type == Type.BEAN;
    }

    @Override
    public boolean isPublicApi()
    {
        return type == Type.ALFRSCO_PUBLIC_API;
    }

    @Override
    public boolean isThirdParty()
    {
        return type == Type.CLASSPATH_ELEMENT;
    }
}
