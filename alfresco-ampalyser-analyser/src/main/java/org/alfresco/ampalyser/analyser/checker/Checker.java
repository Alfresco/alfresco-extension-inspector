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

import org.alfresco.ampalyser.analyser.result.Result;
import org.alfresco.ampalyser.model.InventoryReport;

/**
 * Defines how a checker should work.
 *
 * @author Lucian Tuca
 */
public interface Checker
{
    default List<Result> process(InventoryReport warReport, InventoryReport ampReport, Map<String, Object> extraInfo)
    {
        if (this.canProcessEntry(warReport, ampReport, extraInfo))
        {
            return processInternal(warReport, ampReport, extraInfo);
        }
        return emptyList();
    }

    List<Result> processInternal(InventoryReport warReport, InventoryReport ampReport, Map<String, Object> extraInfo);

    // TODO: Do we need this? Maybe create a type for the checker to correlate it with a field in a Result class?
    Result.Type getType();

    boolean canProcessEntry(InventoryReport warReport, InventoryReport ampReport, Map<String, Object> extraInfo);
}
