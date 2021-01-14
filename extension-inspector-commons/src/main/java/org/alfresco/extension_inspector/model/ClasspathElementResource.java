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

package org.alfresco.extension_inspector.model;

import static org.alfresco.extension_inspector.model.Resource.Type.CLASSPATH_ELEMENT;

import java.io.Serializable;

public class ClasspathElementResource extends AbstractResource implements Serializable
{
    public ClasspathElementResource()
    {
    }

    public ClasspathElementResource(String id, String definingObject)
    {
        super(CLASSPATH_ELEMENT, id, definingObject);
    }

    @Override
    public String toString()
    {
        return "ClasspathElementResource{" +
               "id='" + id + '\'' +
               ", definingObject='" + definingObject + '\'' +
               '}';
    }
}
