/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.service;

import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.inventory.output.InventoryOutput;

public interface InventoryService
{
    InventoryReport extractInventoryReport(String warPath);

    void generateInventoryReport(String warPath, InventoryOutput output);
}
