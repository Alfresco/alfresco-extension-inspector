/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.result;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.MISSING_DEPENDENCY;

import java.util.Objects;
import java.util.Set;

import org.alfresco.ampalyser.model.Resource;

public class MissingDependencyConflict extends AbstractConflict
{
    private Set<String> unsatisfiedDependencies;

    public MissingDependencyConflict()
    {
    }

    public MissingDependencyConflict(Resource ampResourceInConflict, Set<String> unsatisfiedDependencies,
        String alfrescoVersion)
    {
        super(MISSING_DEPENDENCY, ampResourceInConflict, null, alfrescoVersion);
        this.unsatisfiedDependencies = unsatisfiedDependencies;
    }

    public Set<String> getUnsatisfiedDependencies()
    {
        return unsatisfiedDependencies;
    }

    public void setUnsatisfiedDependencies(Set<String> unsatisfiedDependencies)
    {
        this.unsatisfiedDependencies = unsatisfiedDependencies;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MissingDependencyConflict that = (MissingDependencyConflict) o;
        return Objects.equals(unsatisfiedDependencies, that.unsatisfiedDependencies);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), unsatisfiedDependencies);
    }

    @Override
    public String toString()
    {
        return "MissingDependencyConflict{" +
               "unsatisfiedDependencies=" + unsatisfiedDependencies +
               "} " + super.toString();
    }
}
