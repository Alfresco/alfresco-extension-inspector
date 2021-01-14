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

import java.util.Objects;

import org.alfresco.extension_inspector.model.Resource;

/**
 * Base class for various type of conflicts, usually found by the {@link org.alfresco.extension_inspector.analyser.checker.Checker}
 *
 * @author Lucian Tuca
 */
public abstract class AbstractConflict implements Conflict
{
    private Type type;
    private Resource ampResourceInConflict;
    private Resource warResourceInConflict;
    private String alfrescoVersion;

    public AbstractConflict()
    {
    }

    public AbstractConflict(Type type, Resource ampResourceInConflict,
        Resource warResourceInConflict, String alfrescoVersion)
    {
        this.type = type;
        this.ampResourceInConflict = ampResourceInConflict;
        this.warResourceInConflict = warResourceInConflict;
        this.alfrescoVersion = alfrescoVersion;
    }

    @Override
    public Type getType()
    {
        return type;
    }

    @Override
    public void setType(Type type)
    {
        this.type = type;
    }

    @Override
    public Resource getAmpResourceInConflict()
    {
        return ampResourceInConflict;
    }

    @Override
    public void setAmpResourceInConflict(Resource ampResourceInConflict)
    {
        this.ampResourceInConflict = ampResourceInConflict;
    }

    @Override
    public Resource getWarResourceInConflict()
    {
        return warResourceInConflict;
    }

    @Override
    public void setWarResourceInConflict(Resource warResourceInConflict)
    {
        this.warResourceInConflict = warResourceInConflict;
    }

    @Override
    public String getAlfrescoVersion()
    {
        return alfrescoVersion;
    }

    @Override
    public void setAlfrescoVersion(String alfrescoVersion)
    {
        this.alfrescoVersion = alfrescoVersion;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(type, ampResourceInConflict, warResourceInConflict, alfrescoVersion);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractConflict that = (AbstractConflict) o;
        return type == that.type &&
               Objects.equals(ampResourceInConflict, that.ampResourceInConflict) &&
               Objects.equals(warResourceInConflict, that.warResourceInConflict) &&
               Objects.equals(alfrescoVersion, that.alfrescoVersion);
    }

    @Override
    public String toString()
    {
        return "AbstractConflict{" +
            "type=" + type +
            ", ampResourceInConflict='" + ampResourceInConflict + '\'' +
            ", warResourceInConflict='" + warResourceInConflict + '\'' +
            ", alfrescoVersion='" + alfrescoVersion + '\'' +
            '}';
    }
}
