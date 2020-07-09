/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.result;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.CUSTOM_CODE;

import java.util.Objects;
import java.util.Set;

import org.alfresco.ampalyser.model.Resource;

/**
 * Represents a conflict between an .amp class and its class dependencies.
 * e.g. An .amp class using Alfresco classes that are not @AlfrescoPublicAPI annotated
 *
 * @author Lucian Tuca
 */
public class CustomCodeConflict extends AbstractConflict
{
    private Set<String> invalidAlfrescoDependencies;

    public CustomCodeConflict()
    {
    }

    public CustomCodeConflict(Resource ampResourceInConflict, Resource warResourceInConflict,
        Set<String> invalidAlfrescoDependencies, String alfrescoVersion)
    {
        super(CUSTOM_CODE, ampResourceInConflict, warResourceInConflict, alfrescoVersion);
        this.invalidAlfrescoDependencies = invalidAlfrescoDependencies;
    }

    public Set<String> getInvalidAlfrescoDependencies()
    {
        return invalidAlfrescoDependencies;
    }

    public void setInvalidAlfrescoDependencies(Set<String> invalidAlfrescoDependencies)
    {
        this.invalidAlfrescoDependencies = invalidAlfrescoDependencies;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CustomCodeConflict that = (CustomCodeConflict) o;
        return Objects.equals(invalidAlfrescoDependencies, that.invalidAlfrescoDependencies);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), invalidAlfrescoDependencies);
    }

    @Override
    public String toString()
    {
        return "CustomCodeConflict{" +
            "invalidAlfrescoDependencies=" + invalidAlfrescoDependencies +
            "} " + super.toString();
    }
}
