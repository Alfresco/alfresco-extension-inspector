/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.result;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.BEAN_OVERWRITE;

import org.alfresco.ampalyser.model.Resource;

/**
 * Represents a conflict, usually found by {@link org.alfresco.ampalyser.analyser.checker.BeanOverwritingChecker}
 * That can happen when certain .amp resources are copied (by the MMT tool) to the .war
 *
 * @author Lucian Tuca
 */
public class BeanOverwriteConflict extends AbstractConflict
{

    public BeanOverwriteConflict()
    {
        super();
    }

    public BeanOverwriteConflict(Resource ampResourceInConflict, Resource warResourceInConflict, String alfrescoVersion)
    {
        super(BEAN_OVERWRITE, ampResourceInConflict, warResourceInConflict, alfrescoVersion);
    }

    @Override
    public String toString()
    {
        return "BeanOverwriteConflict{} " + super.toString();
    }
}
