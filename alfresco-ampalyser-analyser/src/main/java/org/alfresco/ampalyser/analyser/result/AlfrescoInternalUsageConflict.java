/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.result;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.ALFRESCO_INTERNAL_USAGE;

import java.util.Objects;
import java.util.Set;

import org.alfresco.ampalyser.model.ClasspathElementResource;

/**
 * Represents a conflict between an .amp class and its class dependencies.
 * e.g. An .amp class using Alfresco classes that are not @AlfrescoPublicAPI annotated
 *
 * @author Lucian Tuca
 */
public class AlfrescoInternalUsageConflict extends AbstractConflict
{
    private Set<String> invalidAlfrescoDependencies;

    public AlfrescoInternalUsageConflict()
    {
    }

    public AlfrescoInternalUsageConflict(ClasspathElementResource ampResourceInConflict,
        Set<String> invalidAlfrescoDependencies, String alfrescoVersion)
    {
        super(ALFRESCO_INTERNAL_USAGE, ampResourceInConflict, null, alfrescoVersion);
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
        AlfrescoInternalUsageConflict that = (AlfrescoInternalUsageConflict) o;
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
        return "AlfrescoInternalUsageConflict{" +
            "invalidAlfrescoDependencies=" + invalidAlfrescoDependencies +
            "} " + super.toString();
    }
}
