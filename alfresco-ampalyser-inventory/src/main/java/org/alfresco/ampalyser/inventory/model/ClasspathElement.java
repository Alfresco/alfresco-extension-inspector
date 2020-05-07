/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.model;

import java.util.Objects;

public class ClasspathElement extends AbstractResource
{
    private String name;
    private String object;

    public ClasspathElement(String name, String object)
    {
        this.name = name;
        this.object = object;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getObject()
    {
        return object;
    }

    public void setObject(String object)
    {
        this.object = object;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof ClasspathElement))
            return false;
        ClasspathElement that = (ClasspathElement) o;
        return Objects.equals(name, that.name) && Objects.equals(object, that.object);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, object);
    }

    @Override
    public String toString()
    {
        return "{" + "name='" + name + '\'' + ", object='" + object + '\'' + '}';
    }
}
