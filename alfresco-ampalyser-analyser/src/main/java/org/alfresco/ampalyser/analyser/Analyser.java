/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.alfresco.ampalyser.analyser.checker.Checker;
import org.alfresco.ampalyser.analyser.result.Result;
import org.alfresco.ampalyser.model.InventoryReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Centralises the logic for checking for different type of issues.
 *
 * @author Lucian Tuca
 */
@Component
public class Analyser
{
    @Autowired
    private List<Checker> checkers;

    public List<Result> startAnalysis(InventoryReport warReport, InventoryReport ampReport)
        throws IOException
    {
        List<Result> results = new LinkedList<>();
        for (Checker checker : checkers)
        {
            results.addAll(checker.process(warReport, ampReport));
        }
        return results;
    }
}
