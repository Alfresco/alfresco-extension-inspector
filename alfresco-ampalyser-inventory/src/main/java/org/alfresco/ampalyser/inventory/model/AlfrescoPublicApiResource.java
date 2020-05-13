package org.alfresco.ampalyser.inventory.model;

/**
 * @author Lucian Tuca
 * created on 08/05/2020
 */
public class AlfrescoPublicApiResource extends AbstractResource
{
    private boolean deprecated;

    public AlfrescoPublicApiResource(String name, boolean deprecated)
    {
        super(name, Type.ALFRESCO_PUBLIC_API, null);
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
}
