/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.result;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.CLASSPATH_CONFLICT;

import org.alfresco.ampalyser.model.ClasspathElementResource;

public class ClasspathConflict extends AbstractConflict
{
    public ClasspathConflict()
    {
    }

    public ClasspathConflict(ClasspathElementResource ampResourceInConflict,
        ClasspathElementResource warResourceInConflict, String alfrescoVersion)
    {
        super(CLASSPATH_CONFLICT, ampResourceInConflict, warResourceInConflict, alfrescoVersion);
    }

    @Override
    public String toString()
    {
        return "ClasspathConflict{} " + super.toString();
    }
}
