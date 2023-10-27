/*
 * Copyright 2023 Alfresco Software, Ltd.
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

import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.JAKARTA_MIGRATION_CONFLICT;

import java.util.Objects;
import java.util.Set;

import org.alfresco.extension_inspector.model.ClasspathElementResource;

public class JakartaMigrationConflict extends AbstractConflict
{
    private final Set<String> invalidJakartaMigrationDependencies;

    public JakartaMigrationConflict(ClasspathElementResource ampResourceInConflict,
                                         Set<String> invalidJakartaMigrationDependencies, String alfrescoVersion)
    {
        super(JAKARTA_MIGRATION_CONFLICT, ampResourceInConflict, null, alfrescoVersion);
        this.invalidJakartaMigrationDependencies = invalidJakartaMigrationDependencies;
    }

    public Set<String> getInvalidJakartaMigrationDependencies()
    {
        return invalidJakartaMigrationDependencies;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JakartaMigrationConflict that = (JakartaMigrationConflict) o;
        return Objects.equals(invalidJakartaMigrationDependencies, that.invalidJakartaMigrationDependencies);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), invalidJakartaMigrationDependencies);
    }

    @Override
    public String toString()
    {
        return "JakartaMigrationConflict{" +
                "invalidJakartaMigrationDependencies=" + invalidJakartaMigrationDependencies +
                "} " + super.toString();
    }
}
