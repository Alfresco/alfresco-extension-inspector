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
public interface Resource extends Serializable
{
    enum Type
    {
        FILE,
        BEAN,
        ALFRESCO_PUBLIC_API,
        CLASSPATH_ELEMENT;

        static class Constants
        {
            static final String FILE = "FILE";
            static final String BEAN = "BEAN";
            static final String ALFRESCO_PUBLIC_API = "ALFRESCO_PUBLIC_API";
            static final String CLASSPATH_ELEMENT = "CLASSPATH_ELEMENT";
        }
    }

    Type getType();

    void setType(Type type);

    String getDefiningObject();

    void setDefiningObject(String definingObject);

    String getId();

    void setId(String id);
}
