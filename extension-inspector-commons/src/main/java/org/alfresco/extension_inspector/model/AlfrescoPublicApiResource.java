/*
 * Copyright 2021 Alfresco Software, Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
