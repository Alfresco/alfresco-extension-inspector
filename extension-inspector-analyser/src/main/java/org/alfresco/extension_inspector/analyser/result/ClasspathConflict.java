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

import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.CLASSPATH_CONFLICT;

import org.alfresco.extension_inspector.model.ClasspathElementResource;

public class ClasspathConflict extends AbstractConflict
{
    public ClasspathConflict()
    {
    }

    public ClasspathConflict(ClasspathElementResource ampResourceInConflict,
        ClasspathElementResource warResourceInConflict, String alfrescoVersion)
    {
        super(CLASSPATH_CONFLICT, ampResourceInConflict, warResourceInConflict, alfrescoVersion);
    }

    @Override
    public String toString()
    {
        return "ClasspathConflict{} " + super.toString();
    }
}
