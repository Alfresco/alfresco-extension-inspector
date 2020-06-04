/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import java.util.List;

import org.alfresco.ampalyser.analyser.result.Result;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.springframework.stereotype.Component;

/**
 * Checker for detecting File Overwrites conflicts.
 *
 * @author Lucian Tuca
 */
@Component
public class FileOverridingChecker implements Checker
{
    @Override
    public List<Result> processInternal(InventoryReport warReport, InventoryReport ampRepor)
    {
        return null;
    }

    @Override
    public Resource.Type getType()
    {
        return null;
    }

    @Override
    public boolean canProcessEntry(InventoryReport warReport, InventoryReport ampRepor)
    {
        return false;
    }
}
