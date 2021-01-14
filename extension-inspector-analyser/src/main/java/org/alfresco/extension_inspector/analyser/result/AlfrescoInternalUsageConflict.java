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
package org.alfresco.extension_inspector.analyser.result;

import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.ALFRESCO_INTERNAL_USAGE;

import java.util.Objects;
import java.util.Set;

import org.alfresco.extension_inspector.model.ClasspathElementResource;

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
