/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Map;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.model.InventoryReport;

/**
 * Defines how a checker should work.
 *
 * @author Lucian Tuca
 */
public interface Checker
{
    String ALFRESCO_VERSION = "WAR_ALFRESCO_VERSION";

    default List<Conflict> process(InventoryReport ampInventory, InventoryReport warInventory, Map<String, Object> extraInfo)
    {
        if (canProcess(ampInventory, warInventory, extraInfo))
        {
            return processInternal(ampInventory, warInventory, extraInfo);
        }
        return emptyList();
    }

    List<Conflict> processInternal(InventoryReport ampInventory, InventoryReport warInventory, Map<String, Object> extraInfo);

    boolean canProcess(InventoryReport ampInventory, InventoryReport warInventory, Map<String, Object> extraInfo);
}
