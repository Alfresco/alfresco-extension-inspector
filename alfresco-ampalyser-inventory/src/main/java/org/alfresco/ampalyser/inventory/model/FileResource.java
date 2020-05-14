/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.inventory.model;

import static org.alfresco.ampalyser.inventory.model.Resource.Type.FILE;

import java.io.Serializable;

/**
 * @author Lucian Tuca
 * created on 08/05/2020
 */
public class FileResource extends AbstractResource implements Serializable
{
    public FileResource(String name, String definingObject)
    {
        super(FILE, name, definingObject);
    }

    @Override
    public String toString()
    {
        return "FileResource{" +
               "name='" + name + '\'' +
               ", definingObject='" + definingObject + '\'' +
               '}';
    }
}
