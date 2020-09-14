/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.extension_inspector.model;

import static org.alfresco.extension_inspector.model.Resource.Type.ALFRESCO_PUBLIC_API;

import java.io.Serializable;

/**
 * @author Lucian Tuca
 */
public class AlfrescoPublicApiResource extends AbstractResource implements Serializable
{
    private boolean deprecated;
    private boolean implicit = false;

    public AlfrescoPublicApiResource()
    {
    }

    public AlfrescoPublicApiResource(String id, boolean deprecated)
    {
        super(ALFRESCO_PUBLIC_API, id, null);
        this.deprecated = deprecated;
    }

    public AlfrescoPublicApiResource(String id, boolean deprecated, boolean implicit)
    {
        this(id, deprecated);
        this.implicit = implicit;
    }

    public boolean isDeprecated()
    {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated)
    {
        this.deprecated = deprecated;
    }

    public boolean isImplicit()
    {
        return implicit;
    }

    public void setImplicit(boolean implicit)
    {
        this.implicit = implicit;
    }

    @Override
    public String toString()
    {
        return "AlfrescoPublicApiResource{" +
               "deprecated=" + deprecated +
               ", implicit=" + implicit +
               ", id='" + id + '\'' +
               ", definingObject='" + definingObject + '\'' +
               '}';
    }
}
