/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.extension_inspector.analyser.result;

import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.FILE_OVERWRITE;

import java.util.Map;
import java.util.Objects;

import org.alfresco.extension_inspector.model.FileResource;

/**
 * Represents a conflict, usually found by {@link org.alfresco.extension_inspector.analyser.checker.FileOverwritingChecker}
 * That can happen when certain .amp resources are copied (by the MMT tool) to the .war
 *
 * @author Lucian Tuca
 */
public class FileOverwriteConflict extends AbstractConflict
{
    private Map<String, String> usedMapping;

    public FileOverwriteConflict()
    {
    }

    public FileOverwriteConflict(FileResource ampResourceInConflict, FileResource warResourceInConflict,
        Map<String, String> usedMapping, String alfrescoVersion)
    {
        super(FILE_OVERWRITE, ampResourceInConflict, warResourceInConflict, alfrescoVersion);
        this.usedMapping = usedMapping;
    }

    public Map<String, String> getUsedMapping()
    {
        return usedMapping;
    }

    public void setUsedMapping(Map<String, String> usedMapping)
    {
        this.usedMapping = usedMapping;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FileOverwriteConflict that = (FileOverwriteConflict) o;
        return Objects.equals(usedMapping, that.usedMapping);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), usedMapping);
    }

    @Override
    public String toString()
    {
        return "FileOverwriteConflict{" +
               "usedMapping=" + usedMapping +
               "} " + super.toString();
    }
}
