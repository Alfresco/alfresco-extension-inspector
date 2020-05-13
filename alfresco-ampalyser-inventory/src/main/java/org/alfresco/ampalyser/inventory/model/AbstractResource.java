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
    protected String name;
    protected Resource.Type type;
    protected String definingObject;

    public AbstractResource()
    {
    }

    public AbstractResource(String name, Type type, String definingObject)
    {
        this.name = name;
        this.type = type;
        this.definingObject = definingObject;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public Resource.Type getType()
    {
        return type;
    }

    public String getDefiningObject()
    {
        return definingObject;
    }

    public void setDefiningObject(String definingObject)
    {
        this.definingObject = definingObject;
    }
}
