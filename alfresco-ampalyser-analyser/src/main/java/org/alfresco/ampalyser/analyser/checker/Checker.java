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

import org.alfresco.ampalyser.analyser.result.Result;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;

/**
 * Defines how a checker should work.
 *
 * @author Lucian Tuca
 */
public interface Checker
{
    default List<Result> process(InventoryReport warReport, InventoryReport ampReport)
    {
        if (this.canProcessEntry(warReport, ampReport))
        {
            return processInternal(warReport, ampReport);
        }
        return emptyList();
    }

    List<Result> processInternal(InventoryReport warReport, InventoryReport ampRepor);

    Resource.Type getType();

    boolean canProcessEntry(InventoryReport warReport, InventoryReport ampRepor);
}
