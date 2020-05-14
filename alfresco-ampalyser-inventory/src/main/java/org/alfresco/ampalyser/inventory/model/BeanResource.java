/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.model;

import static org.alfresco.ampalyser.inventory.model.Resource.Type.BEAN;

import java.io.Serializable;
import java.util.Objects;

public class BeanResource extends AbstractResource implements Serializable
{
    private String id;

    public BeanResource(String id, String name, String definingObject)
    {
        super(BEAN, name, definingObject);
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BeanResource that = (BeanResource) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), id);
    }

    @Override
    public String toString()
    {
        return "BeanResource{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", definingObject='" + definingObject + '\'' +
               '}';
    }
}
