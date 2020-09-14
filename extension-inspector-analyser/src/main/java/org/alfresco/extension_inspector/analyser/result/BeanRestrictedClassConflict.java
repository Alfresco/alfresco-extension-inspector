/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.extension_inspector.analyser.result;

import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.BEAN_RESTRICTED_CLASS;

import org.alfresco.extension_inspector.model.BeanResource;

/**
 * @author Lucian Tuca
 */
public class BeanRestrictedClassConflict extends AbstractConflict
{
    public BeanRestrictedClassConflict()
    {
    }

    public BeanRestrictedClassConflict(BeanResource ampResourceInConflict, String alfrescoVersion)
    {
        super(BEAN_RESTRICTED_CLASS, ampResourceInConflict, null, alfrescoVersion);
    }

    @Override
    public String toString()
    {
        return "RestrictedBeanClassConflict{} " + super.toString();
    }
}
