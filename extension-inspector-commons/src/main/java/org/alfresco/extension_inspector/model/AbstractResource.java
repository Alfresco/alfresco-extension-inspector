/*
 * Copyright 2021 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfresco.extension_inspector.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Lucian Tuca
 */
public abstract class AbstractResource implements Resource, Serializable
{
    private Type type;
    protected String id;
    protected String definingObject;

    public AbstractResource()
    {
    }

    protected AbstractResource(Type type, String id, String definingObject)
    {
        this.type = type;
        this.id = id;
        this.definingObject = definingObject;
    }

    @Override
    public Type getType()
    {
        return type;
    }

    @Override
    public void setType(Type type)
    {
        this.type = type;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String getDefiningObject()
    {
        return definingObject;
    }

    @Override
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
               Objects.equals(id, that.id) &&
               Objects.equals(definingObject, that.definingObject);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(type, id, definingObject);
    }

    @Override
    public String toString()
    {
        return "AbstractResource{" +
               "type=" + type +
               ", id='" + id + '\'' +
               ", definingObject='" + definingObject + '\'' +
               '}';
    }
}
