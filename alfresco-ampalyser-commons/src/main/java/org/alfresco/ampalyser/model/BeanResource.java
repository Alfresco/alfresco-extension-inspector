/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.model;

import java.io.Serializable;

public class BeanResource extends AbstractResource implements Serializable
{
    public BeanResource(String id, String definingObject)
    {
        super(Type.BEAN, id, definingObject);
    }

    @Override
    public String toString()
    {
        return "BeanResource{" +
            "id='" + id + '\'' +
            ", definingObject='" + definingObject + '\'' +
            '}';
    }
}
