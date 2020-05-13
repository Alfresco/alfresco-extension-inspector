package org.alfresco.ampalyser.inventory.model;

import static org.alfresco.ampalyser.inventory.model.Resource.Type.ALFRESCO_PUBLIC_API;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Lucian Tuca
 * created on 08/05/2020
 */
public class AlfrescoPublicApiResource extends AbstractResource implements Serializable
{
    private boolean deprecated;

    public AlfrescoPublicApiResource(String name, boolean deprecated)
    {
        super(ALFRESCO_PUBLIC_API, name, null);
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
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AlfrescoPublicApiResource that = (AlfrescoPublicApiResource) o;
        return deprecated == that.deprecated;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), deprecated);
    }

    @Override
    public String toString()
    {
        return "AlfrescoPublicApiResource{" +
               "deprecated=" + deprecated +
               ", name='" + name + '\'' +
               ", definingObject='" + definingObject + '\'' +
               '}';
    }
}
