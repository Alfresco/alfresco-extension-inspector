/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.model;

import java.util.Objects;

/**
 * @author Lucian Tuca
 * created on 07/05/2020
 */
public abstract class AbstractResource implements Resource
{
    private String name;
    private Resource.Type type;
    protected String definingObject;

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
        return type == Type.ALFRESCO_PUBLIC_API;
    }

    @Override
    public boolean isClasspathElement()
    {
        return type == Type.CLASSPATH_ELEMENT;
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
        return Objects.equals(name, that.name) &&
               type == that.type &&
               Objects.equals(definingObject, that.definingObject);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, type, definingObject);
    }

    @Override
    public String toString()
    {
        return "AbstractResource{" +
               "name='" + name + '\'' +
               ", type=" + type +
               ", definingObject='" + definingObject + '\'' +
               '}';
    }
}
