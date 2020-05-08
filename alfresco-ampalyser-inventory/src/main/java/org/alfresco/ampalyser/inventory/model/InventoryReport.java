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
    private List<FileResource> files = new ArrayList<>();
    private List<ClasspathElementResource> classpath = new ArrayList<>();
    private List<BeanResource> beanResources = new ArrayList<>();
    private List<AlfrescoPublicApiResource> alfrescoPublicApi = new ArrayList<>();

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public List<FileResource> getFiles()
    {
        return files;
    }

    public void addResource(FileResource file)
    {
        this.files.add(file);
    }

    public List<ClasspathElementResource> getClasspath()
    {
        return classpath;
    }

    public void addClasspathElement(ClasspathElementResource classpathElementResource)
    {
        this.classpath.add(classpathElementResource);
    }

    public List<BeanResource> getBeanResources()
    {
        return beanResources;
    }

    public void addBean(BeanResource beanResource)
    {
        this.beanResources.add(beanResource);
    }

    public List<AlfrescoPublicApiResource> getAlfrescoPublicApi()
    {
        return alfrescoPublicApi;
    }

    public void addAlfrescoPublicApi(AlfrescoPublicApiResource alfrescoPublicApi)
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
        return Objects.equals(version, that.version) && Objects.equals(files, that.files)
            && Objects.equals(classpath, that.classpath) && Objects.equals(
            beanResources, that.beanResources)
            && Objects.equals(alfrescoPublicApi, that.alfrescoPublicApi);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(version, files, classpath, beanResources, alfrescoPublicApi);
    }

    @Override
    public String toString()
    {
        return "InventoryReport{" + "version='" + version + '\'' + ", resources=" + files
            + ", classpath=" + classpath + ", beans=" + beanResources + ", alfrescoPublicApi="
            + alfrescoPublicApi + '}';
    }
}
