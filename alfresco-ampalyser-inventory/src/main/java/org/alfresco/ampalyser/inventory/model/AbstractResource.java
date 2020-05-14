/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Lucian Tuca
 * created on 07/05/2020
 */
public abstract class AbstractResource implements Resource, Serializable
{
    private final Type type;
    protected String name;
    protected String definingObject;

    public AbstractResource()
    {
    }

    protected AbstractResource(Type type, String name, String definingObject)
    {
        this.type = type;
        this.name = name;
        this.definingObject = definingObject;
    }

    @Override
    public Resource.Type getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDefiningObject()
    {
        return definingObject;
    }

    public void setDefiningObject(String definingObject)
    {
        this.definingObject = definingObject;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractResource that = (AbstractResource) o;
        return type == that.type &&
               Objects.equals(name, that.name) &&
               Objects.equals(definingObject, that.definingObject);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(type, name, definingObject);
    }

    @Override
    public String toString()
    {
        return "AbstractResource{" +
               "type=" + type +
               ", name='" + name + '\'' +
               ", definingObject='" + definingObject + '\'' +
               '}';
    }
}
