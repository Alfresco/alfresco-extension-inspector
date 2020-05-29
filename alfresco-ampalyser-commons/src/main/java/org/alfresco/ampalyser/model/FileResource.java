/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.model;

import static org.alfresco.ampalyser.model.Resource.Type.FILE;

import java.io.Serializable;

/**
 * @author Lucian Tuca
 */
public class FileResource extends AbstractResource implements Serializable
{
    public FileResource(String id, String definingObject)
    {
        super(FILE, id, definingObject);
    }

    @Override
    public String toString()
    {
        return "FileResource{" +
               "id='" + id + '\'' +
               ", definingObject='" + definingObject + '\'' +
               '}';
    }
}
