/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.result;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.Constants.BEAN_OVERWRITE;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.Constants.CLASSPATH_CONFLICT;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.Constants.ALFRESCO_INTERNAL_USAGE;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.Constants.FILE_OVERWRITE;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.Constants.BEAN_RESTRICTED_CLASS;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.Constants.WAR_LIBRARY_USAGE;

import org.alfresco.ampalyser.model.Resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents the result that a {@link org.alfresco.ampalyser.analyser.checker.Checker} can find.
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
