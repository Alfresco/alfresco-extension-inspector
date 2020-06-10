/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.result;

import static org.alfresco.ampalyser.analyser.result.Result.Type.FILE_OVERWRITE;

import java.util.Map;

import org.alfresco.ampalyser.model.Resource;

/**
 * Represents a conflict, usually found by {@link org.alfresco.ampalyser.analyser.checker.FileOverwritingChecker}
 * That can happen when certain .amp resources are copied (by the MMT tool) to the .war
 *
 * @author Lucian Tuca
 */
public class FileOverwriteResult extends AbstractResult
{
    private Map<String, String> usedMapping;

    public FileOverwriteResult()
    {
        super();
    }

    public FileOverwriteResult(Resource ampResourceInConflict,
        Resource warResourceInConflict, Map<String, String> usedMapping)
    {
        super(FILE_OVERWRITE, ampResourceInConflict, warResourceInConflict);
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
}
