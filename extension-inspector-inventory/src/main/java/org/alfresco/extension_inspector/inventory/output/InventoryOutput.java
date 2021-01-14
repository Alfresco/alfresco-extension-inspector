/*
 * Copyright 2021 Alfresco Software, Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

package org.alfresco.extension_inspector.inventory.output;

import static java.text.MessageFormat.format;

import org.alfresco.extension_inspector.model.InventoryReport;

public interface InventoryOutput
{
    enum OutputType
    {
        JSON
    }

    String DEFAULT_REPORT_PATH = "{0}.inventory.{1}";

    void generateOutput(InventoryReport report);

    /**
     * @param sourceName the name of the war for which the inventory report is generated
     * @param type the type of the output
     * @return the default name of the output file
     */
    default String defaultPath(String sourceName, OutputType type)
    {
        return  format(DEFAULT_REPORT_PATH, sourceName, type.name().toLowerCase());
    }
}
