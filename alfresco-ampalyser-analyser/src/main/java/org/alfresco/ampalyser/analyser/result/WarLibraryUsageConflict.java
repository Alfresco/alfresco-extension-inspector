/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.result;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.WAR_LIBRARY_USAGE;

import java.util.Objects;
import java.util.Set;

import org.alfresco.ampalyser.model.Resource;

public class WarLibraryUsageConflict extends AbstractConflict
{
    private Set<String> classDependencies;

    public WarLibraryUsageConflict()
    {
    }

    public WarLibraryUsageConflict(Resource ampResourceInConflict, Set<String> classDependencies, String alfrescoVersion
    )
    {
        super(WAR_LIBRARY_USAGE, ampResourceInConflict, null, alfrescoVersion);
        this.classDependencies = classDependencies;
    }

    public Set<String> getClassDependencies()
    {
        return classDependencies;
    }

    public void setClassDependencies(Set<String> classDependencies)
    {
        this.classDependencies = classDependencies;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WarLibraryUsageConflict that = (WarLibraryUsageConflict) o;
        return Objects.equals(classDependencies, that.classDependencies);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), classDependencies);
    }

    @Override
    public String toString()
    {
        return "WarLibraryUsageConflict{" +
               "classDependencies=" + classDependencies +
               "} " + super.toString();
    }
}
