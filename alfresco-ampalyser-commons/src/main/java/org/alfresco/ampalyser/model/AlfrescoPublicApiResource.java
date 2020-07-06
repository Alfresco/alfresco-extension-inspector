package org.alfresco.ampalyser.model;

import static org.alfresco.ampalyser.model.Resource.Type.ALFRESCO_PUBLIC_API;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Lucian Tuca
 */
public class AlfrescoPublicApiResource extends AbstractResource implements Serializable
{
    private boolean deprecated;

    public AlfrescoPublicApiResource()
    {
    }

    public AlfrescoPublicApiResource(String id, boolean deprecated)
    {
        super(ALFRESCO_PUBLIC_API, id, null);
        this.deprecated = deprecated;
    }

    public boolean isDeprecated()
    {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated)
    {
        this.deprecated = deprecated;
    }

    @Override
    public String toString()
    {
        return "AlfrescoPublicApiResource{" +
               "deprecated=" + deprecated +
               ", id='" + id + '\'' +
               ", definingObject='" + definingObject + '\'' +
               '}';
    }
}
