/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InventoryReport
{
    private String version;
    private List<String> resources = new ArrayList<>();
    private List<ClasspathElement> classpath = new ArrayList<>();
    private List<Bean> beans = new ArrayList<>();
    private List<String> alfrescoPublicApi = new ArrayList<>();

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public List<String> getResources()
    {
        return resources;
    }

    public void addResource(String resource)
    {
        this.resources.add(resource);
    }

    public List<ClasspathElement> getClasspath()
    {
        return classpath;
    }

    public void addClasspathElement(ClasspathElement classpathElement)
    {
        this.classpath.add(classpathElement);
    }

    public List<Bean> getBeans()
    {
        return beans;
    }

    public void addBean(Bean bean)
    {
        this.beans.add(bean);
    }

    public List<String> getAlfrescoPublicApi()
    {
        return alfrescoPublicApi;
    }

    public void addAlfrescoPublicApi(String alfrescoPublicApi)
    {
        this.alfrescoPublicApi.add(alfrescoPublicApi);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof InventoryReport))
            return false;
        InventoryReport that = (InventoryReport) o;
        return Objects.equals(version, that.version) && Objects.equals(resources, that.resources)
            && Objects.equals(classpath, that.classpath) && Objects.equals(beans, that.beans)
            && Objects.equals(alfrescoPublicApi, that.alfrescoPublicApi);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(version, resources, classpath, beans, alfrescoPublicApi);
    }

    @Override
    public String toString()
    {
        return "InventoryReport{" + "version='" + version + '\'' + ", resources=" + resources
            + ", classpath=" + classpath + ", beans=" + beans + ", alfrescoPublicApi="
            + alfrescoPublicApi + '}';
    }
}
