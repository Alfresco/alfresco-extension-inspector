package org.alfresco.ampalyser.inventory.model;

/**
 * @author Lucian Tuca
 * created on 08/05/2020
 */
public class AlfrescoPublicApiResource extends AbstractResource
{
    private boolean deprecated;

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
                "class=" + getName() +
                ", deprecated=" + deprecated + '}';
    }
}
