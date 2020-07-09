/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.result;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.RESTRICTED_BEAN_CLASS;

import org.alfresco.ampalyser.model.Resource;

/**
 * @author Lucian Tuca
 */
public class RestrictedBeanClassConflict extends AbstractConflict
{
    public RestrictedBeanClassConflict()
    {
    }

    public RestrictedBeanClassConflict(Resource ampResourceInConflict, String alfrescoVersion)
    {
        super(RESTRICTED_BEAN_CLASS, ampResourceInConflict, null, alfrescoVersion);
    }

    @Override
    public String toString()
    {
        return "RestrictedBeanClassConflict{} " + super.toString();
    }
}
