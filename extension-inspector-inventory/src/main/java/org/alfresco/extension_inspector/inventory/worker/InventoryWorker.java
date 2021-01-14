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

package org.alfresco.extension_inspector.inventory.worker;

import static java.util.Collections.emptySet;

import java.util.Set;
import java.util.zip.ZipEntry;

import org.alfresco.extension_inspector.model.Resource;

/**
 * @author Denis Ungureanu
 * @author Lucian Tuca
 */
public interface InventoryWorker
{
    default Set<Resource> processZipEntry(ZipEntry entry, byte[] data, String definingObject)
    {
        if (this.canProcessEntry(entry, definingObject))
        {
            return processInternal(entry, data, definingObject);
        }
        return emptySet();
    }

    Set<Resource> processInternal(ZipEntry entry, byte[] data, String definingObject);

    Resource.Type getType();

    boolean canProcessEntry(ZipEntry entry, String definingObject);
}
