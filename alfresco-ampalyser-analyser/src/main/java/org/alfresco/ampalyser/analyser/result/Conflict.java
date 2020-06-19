/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.result;

import org.alfresco.ampalyser.model.Resource;

/**
 * Represents the result that a {@link org.alfresco.ampalyser.analyser.checker.Checker} can find.
 * @author Lucian Tuca
 */
public interface Conflict
{
    enum Type
    {
        FILE_OVERWRITE,
        BEAN_OVERWRITE,
        RESTRICTED_BEAN_CLASS,
        CLASSPATH_CONFLICT;

        public static class Constants
        {
            public static final String FILE_OVERWRITE= "FILE_OVERWRITE";
            public static final String BEAN_OVERWRITE= "BEAN_OVERWRITE";
            public static final String RESTRICTED_BEAN_CLASS= "RESTRICTED_BEAN_CLASS";
            public static final String CLASSPATH_CONFLICT = "CLASSPATH_CONFLICT";
        }
    }

    String getId();
    void setId(String id);

    Type getType();
    void setType(Type type);

    Resource getAmpResourceInConflict();
    void setAmpResourceInConflict(Resource ampResourceInConflict);

    Resource getWarResourceInConflict();
    void setWarResourceInConflict(Resource warResourceInConflict);

    String getAlfrescoVersion();
    void setAlfrescoVersion(String alfrescoVersion);
}
