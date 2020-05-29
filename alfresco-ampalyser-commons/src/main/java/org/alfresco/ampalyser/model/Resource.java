/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.model;

import static org.alfresco.ampalyser.model.Resource.Type.ALFRESCO_PUBLIC_API;
import static org.alfresco.ampalyser.model.Resource.Type.BEAN;
import static org.alfresco.ampalyser.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.alfresco.ampalyser.model.Resource.Type.FILE;

import java.io.Serializable;

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
    @JsonSubTypes.Type(value = AlfrescoPublicApiResource.class, name = Resource.Type.Constants.ALFRESCO_PUBLIC_API),
    @JsonSubTypes.Type(value = BeanResource.class, name = Resource.Type.Constants.BEAN),
    @JsonSubTypes.Type(value = ClasspathElementResource.class, name = Resource.Type.Constants.CLASSPATH_ELEMENT),
    @JsonSubTypes.Type(value = FileResource.class, name = Resource.Type.Constants.FILE),
})
@JsonIgnoreProperties(ignoreUnknown = true)
public interface Resource extends Serializable
{
    enum Type
    {
        FILE(Constants.FILE),
        BEAN(Constants.BEAN),
        ALFRESCO_PUBLIC_API(Constants.ALFRESCO_PUBLIC_API),
        CLASSPATH_ELEMENT(Constants.CLASSPATH_ELEMENT);

        public static class Constants
        {
            public static final String FILE = "FILE";
            public static final String BEAN = "BEAN";
            public static final String ALFRESCO_PUBLIC_API = "ALFRESCO_PUBLIC_API";
            public static final String CLASSPATH_ELEMENT = "CLASSPATH_ELEMENT";
        }

        private final String value;

        Type(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    Type getType();

    void setType(Type type);

    String getDefiningObject();

    void setDefiningObject(String definingObject);

    String getId();

    void setId(String id);

    static boolean isFile(final Resource r)
    {
        return r != null && r.getType() == FILE;
    }

    static boolean isBean(final Resource r)
    {
        return r != null && r.getType() == BEAN;
    }

    static boolean isPublicApi(final Resource r)
    {
        return r != null && r.getType() == ALFRESCO_PUBLIC_API;
    }

    static boolean isClasspathElement(final Resource r)
    {
        return r != null && r.getType() == CLASSPATH_ELEMENT;
    }
}
