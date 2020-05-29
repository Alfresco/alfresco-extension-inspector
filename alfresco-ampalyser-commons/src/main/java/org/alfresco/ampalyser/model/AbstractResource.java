/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.model;

import static org.alfresco.ampalyser.model.Resource.Type.Constants.ALFRESCO_PUBLIC_API;
import static org.alfresco.ampalyser.model.Resource.Type.Constants.BEAN;
import static org.alfresco.ampalyser.model.Resource.Type.Constants.CLASSPATH_ELEMENT;
import static org.alfresco.ampalyser.model.Resource.Type.Constants.FILE;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Lucian Tuca
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AlfrescoPublicApiResource.class, name = ALFRESCO_PUBLIC_API),
    @JsonSubTypes.Type(value = BeanResource.class, name = BEAN),
    @JsonSubTypes.Type(value = ClasspathElementResource.class, name = CLASSPATH_ELEMENT),
    @JsonSubTypes.Type(value = FileResource.class, name = FILE),
})
@JsonIgnoreProperties(ignoreUnknown = true)
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
