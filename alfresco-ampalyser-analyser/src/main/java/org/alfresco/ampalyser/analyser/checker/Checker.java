/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.stream.Stream.empty;

import java.util.stream.Stream;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.model.InventoryReport;

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
}
