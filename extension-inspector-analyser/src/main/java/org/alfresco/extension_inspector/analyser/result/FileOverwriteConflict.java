/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
