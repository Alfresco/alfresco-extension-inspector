/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.extension_inspector.model;

import static org.alfresco.extension_inspector.model.Resource.Type.BEAN;

import java.io.Serializable;
import java.util.Objects;

public class BeanResource extends AbstractResource implements Serializable
{
    private String beanClass;

    public BeanResource()
    {
    }

    public BeanResource(String id, String definingObject, String beanClass)
    {
        super(BEAN, id, definingObject);
        this.beanClass = beanClass;
    }

    public String getBeanClass()
    {
        return beanClass;
    }

    public void setBeanClass(String beanClass)
    {
        this.beanClass = beanClass;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BeanResource that = (BeanResource) o;
        return Objects.equals(beanClass, that.beanClass);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), beanClass);
    }

    @Override
    public String toString()
    {
        return "BeanResource{" +
            "id='" + id + '\'' +
            ", definingObject='" + definingObject + '\'' +
            ", beanClass='" + beanClass + '\'' +
            '}';
    }
}
