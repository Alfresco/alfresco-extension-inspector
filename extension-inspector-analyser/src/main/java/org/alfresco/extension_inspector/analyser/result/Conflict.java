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
package org.alfresco.extension_inspector.analyser.result;

import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.Constants.BEAN_OVERWRITE;
import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.Constants.CLASSPATH_CONFLICT;
import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.Constants.ALFRESCO_INTERNAL_USAGE;
import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.Constants.FILE_OVERWRITE;
import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.Constants.BEAN_RESTRICTED_CLASS;
import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.Constants.WAR_LIBRARY_USAGE;

import org.alfresco.extension_inspector.model.Resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents the result that a {@link org.alfresco.extension_inspector.analyser.checker.Checker} can find.
 * @author Lucian Tuca
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes({
                  @JsonSubTypes.Type(value = BeanOverwriteConflict.class, name = BEAN_OVERWRITE),
                  @JsonSubTypes.Type(value = BeanRestrictedClassConflict.class, name = BEAN_RESTRICTED_CLASS),
                  @JsonSubTypes.Type(value = FileOverwriteConflict.class, name = FILE_OVERWRITE),
                  @JsonSubTypes.Type(value = ClasspathConflict.class, name = CLASSPATH_CONFLICT),
                  @JsonSubTypes.Type(value = AlfrescoInternalUsageConflict.class, name = ALFRESCO_INTERNAL_USAGE),
                  @JsonSubTypes.Type(value = WarLibraryUsageConflict.class, name = WAR_LIBRARY_USAGE),
              })
public interface Conflict
{
    enum Type
    {
        FILE_OVERWRITE,
        BEAN_OVERWRITE,
        BEAN_RESTRICTED_CLASS,
        CLASSPATH_CONFLICT, ALFRESCO_INTERNAL_USAGE,
        WAR_LIBRARY_USAGE,
        ;

        static class Constants
        {
            static final String FILE_OVERWRITE = "FILE_OVERWRITE";
            static final String BEAN_OVERWRITE = "BEAN_OVERWRITE";
            static final String BEAN_RESTRICTED_CLASS = "BEAN_RESTRICTED_CLASS";
            static final String CLASSPATH_CONFLICT = "CLASSPATH_CONFLICT";
            static final String ALFRESCO_INTERNAL_USAGE = "ALFRESCO_INTERNAL_USAGE";
            static final String WAR_LIBRARY_USAGE = "WAR_LIBRARY_USAGE";
        }
    }

    Type getType();
    void setType(Type type);

    Resource getAmpResourceInConflict();
    void setAmpResourceInConflict(Resource ampResourceInConflict);

    Resource getWarResourceInConflict();
    void setWarResourceInConflict(Resource warResourceInConflict);

    String getAlfrescoVersion();
    void setAlfrescoVersion(String alfrescoVersion);
}
