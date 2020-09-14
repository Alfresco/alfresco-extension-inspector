/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.extension_inspector.inventory.service;

import org.alfresco.extension_inspector.model.InventoryReport;
import org.alfresco.extension_inspector.inventory.output.InventoryOutput;

public interface InventoryService
{
    InventoryReport extractInventoryReport(String warPath);

    void generateInventoryReport(String warPath, InventoryOutput output);
}
