/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.model;

import static org.alfresco.ampalyser.inventory.model.Resource.Type.CLASSPATH_ELEMENT;

import java.io.Serializable;

public class ClasspathElementResource extends AbstractResource implements Serializable
{
    public ClasspathElementResource()
    {
        this.type = CLASSPATH_ELEMENT;
    }

    public ClasspathElementResource(String name, String definingObject)
    {
        super(CLASSPATH_ELEMENT, name, definingObject);
    }

    @Override
    public String toString()
    {
        return "ClasspathElementResource{" +
               "name='" + name + '\'' +
               ", definingObject='" + definingObject + '\'' +
               '}';
    }
}
