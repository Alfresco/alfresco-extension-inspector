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
