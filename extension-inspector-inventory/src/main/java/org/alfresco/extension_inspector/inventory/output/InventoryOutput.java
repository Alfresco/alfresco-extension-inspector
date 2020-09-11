/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
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
