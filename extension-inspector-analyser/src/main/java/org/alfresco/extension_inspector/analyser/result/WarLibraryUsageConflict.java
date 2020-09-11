/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.extension_inspector.analyser.result;

import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.WAR_LIBRARY_USAGE;

import java.util.Objects;
import java.util.Set;

import org.alfresco.extension_inspector.model.ClasspathElementResource;
import org.alfresco.extension_inspector.model.Resource;

public class WarLibraryUsageConflict extends AbstractConflict
{
    private Set<Resource> dependencies;

    public WarLibraryUsageConflict()
    {
    }

    public WarLibraryUsageConflict(ClasspathElementResource ampResourceInConflict, Set<Resource> dependencies,
        String alfrescoVersion)
    {
        super(WAR_LIBRARY_USAGE, ampResourceInConflict, null, alfrescoVersion);
        this.dependencies = dependencies;
    }

    public Set<Resource> getDependencies()
    {
        return dependencies;
    }

    public void setDependencies(Set<Resource> dependencies)
    {
        this.dependencies = dependencies;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WarLibraryUsageConflict that = (WarLibraryUsageConflict) o;
        return Objects.equals(dependencies, that.dependencies);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), dependencies);
    }

    @Override
    public String toString()
    {
        return "WarLibraryUsageConflict{" +
               "dependencies=" + dependencies +
               "} " + super.toString();
    }
}
