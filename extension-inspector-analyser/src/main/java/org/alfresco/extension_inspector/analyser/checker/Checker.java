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
package org.alfresco.extension_inspector.analyser.checker;

import static java.util.stream.Stream.empty;

import java.util.Set;
import java.util.stream.Stream;

import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.model.InventoryReport;

/**
 * Defines how a checker should work.
 *
 * @author Lucian Tuca
 */
public interface Checker
{
    default Stream<Conflict> process(InventoryReport warInventory, String alfrescoVersion)
    {
        if (canProcess(warInventory, alfrescoVersion))
        {
            return processInternal(warInventory, alfrescoVersion);
        }
        return empty();
    }

    Stream<Conflict> processInternal(InventoryReport warInventory, String alfrescoVersion);

    boolean canProcess(InventoryReport warInventory, String alfrescoVersion);

    /**
     * This method checks whether or not the provided className (in `/package/name/ClassName.class` format)
     * is matched in the provided 'allowedList' by the * patterns or by the class itself
     *
     * @param className
     * @return
     */
    static boolean isInAllowedList(String className, Set<String> allowedList)
    {
        final String[] packs = className.split("[./]");
        if (packs.length < 3)
        {
            return false;
        }
        StringBuilder pack = new StringBuilder(packs[1]).append('/').append(packs[2]);
        for (int i = 3; i < packs.length - 1; i++)
        {
            if (allowedList.contains(pack.toString()))
            {
                return true;
            }
            pack.append("/").append(packs[i]);
        }

        return allowedList.contains(pack.toString());
    }
}
