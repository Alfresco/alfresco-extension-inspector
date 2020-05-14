/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.alfresco.ampalyser.inventory.utils.InventoryUtils;

public class InventoryReport
{
    private String version;
    private Map<Resource.Type, List<Resource>> resources = new TreeMap<>(new SortByType());

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public Map<Resource.Type, List<Resource>> getResources()
    {
        return resources;
    }

    public void setResources(Map<Resource.Type, List<Resource>> resources)
    {
        this.resources = resources;
    }

    public void addResources(Map<Resource.Type, List<Resource>> resources)
    {
        resources.keySet().stream().forEach(
            type -> this.resources.merge(type, resources.get(type), InventoryUtils::mergeLists));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof InventoryReport))
            return false;
        InventoryReport report = (InventoryReport) o;
        return Objects.equals(version, report.version) && Objects
            .equals(resources, report.resources);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(version, resources);
    }

    @Override
    public String toString()
    {
        return "InventoryReport{" + "version='" + version + '\'' + ", resources=" + resources + '}';
    }
}
