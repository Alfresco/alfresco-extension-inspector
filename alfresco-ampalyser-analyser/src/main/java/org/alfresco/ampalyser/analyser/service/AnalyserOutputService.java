/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.printers.ConflictPrinter;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyserOutputService
{
    @Autowired
    private List<ConflictPrinter> printers;

    public void print(Map<Conflict.Type, Map<String, Set<Conflict>>> conflictPerTypeAndResourceId,
        boolean verboseOutput)
    {
        printers
            .forEach(p -> 
                p.print(conflictPerTypeAndResourceId.get(p.getConflictType()), verboseOutput));

        if (!verboseOutput)
        {
            System.out.println("(use option --verbose for version details)");
        }
    }
}
