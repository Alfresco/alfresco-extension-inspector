/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.model;

public class ClasspathElementResource extends AbstractResource
{
    public ClasspathElementResource()
    {
        this.type = Type.CLASSPATH_ELEMENT;
    }

    public ClasspathElementResource(String name, String definingObject)
    {
        super(name, Type.CLASSPATH_ELEMENT, definingObject);
    }
}
