/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.service;

import org.alfresco.ampalyser.inventory.ResourceProcessor;
import org.alfresco.ampalyser.inventory.model.InventoryReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService
{
    @Autowired
    private ResourceProcessor resourceProcessor;

    @Override
    public InventoryReport extractInventoryReport(String warPath)
    {
        //TODO add logic
        return null;
    }
}
