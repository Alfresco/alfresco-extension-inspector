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

import static org.alfresco.extension_inspector.model.Resource.Type.Constants.ALFRESCO_PUBLIC_API;
import static org.alfresco.extension_inspector.model.Resource.Type.Constants.BEAN;
import static org.alfresco.extension_inspector.model.Resource.Type.Constants.CLASSPATH_ELEMENT;
import static org.alfresco.extension_inspector.model.Resource.Type.Constants.FILE;

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
