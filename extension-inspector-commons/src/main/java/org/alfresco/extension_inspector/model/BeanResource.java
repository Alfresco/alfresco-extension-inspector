/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
