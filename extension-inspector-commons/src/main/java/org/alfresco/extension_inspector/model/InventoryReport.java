/*
 * Copyright 2021 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.extension_inspector.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.alfresco.extension_inspector.commons.InventoryUtils;
import org.alfresco.extension_inspector.model.Resource.Type;

public class InventoryReport
{
    public static final String SCHEMA_VERSION = "1.0";
    public static final String IMPLEMENTATION_VERSION = "IMPLEMENTATION_VERSION";
    public static final String SPECIFICATION_VERSION = "SPECIFICATION_VERSION";

    private String schemaVersion;
    private String alfrescoVersion;
    private Map<Type, Set<Resource>> resources = new EnumMap<>(Type.class);

    public InventoryReport()
    {
        this.schemaVersion = SCHEMA_VERSION;
    }

    public String getSchemaVersion()
    {
        return this.schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion)
    {
        this.schemaVersion = schemaVersion;
    }

    public String getAlfrescoVersion()
    {
        return alfrescoVersion;
    }

    public void setAlfrescoVersion(String alfrescoVersion)
    {
        this.alfrescoVersion = alfrescoVersion;
    }

    public Map<Type, Set<Resource>> getResources()
    {
        return resources;
    }

    public void setResources(Map<Type, Set<Resource>> resources)
    {
        this.resources = resources;
    }

    public void addResources(Map<Type, Set<Resource>> resources)
    {
        resources.forEach((k, v) -> this.resources.merge(k, v, InventoryUtils::mergeCollections));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof InventoryReport))
            return false;
        InventoryReport report = (InventoryReport) o;
        return Objects.equals(alfrescoVersion, report.alfrescoVersion) &&
                Objects.equals(resources, report.resources);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(alfrescoVersion, resources);
    }

    @Override
    public String toString()
    {
        return "InventoryReport{" + "alfrescoVersion='" + alfrescoVersion + '\'' + ", resources=" + resources + '}';
    }
}
