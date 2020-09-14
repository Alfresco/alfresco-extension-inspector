/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
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