/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.printers.ConflictPrinter.joinWarResourceIds;

import java.util.Set;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.springframework.stereotype.Component;

@Component
public class NonPublicApiUsageConflictPrinter implements ConflictPrinter
{
    private static final String HEADER =
        "Found usage of internal Alfresco classes! Alfresco provides a Java API "
            + "that is clearly marked as @AlfrescoPublicAPI. Any other classes or interfaces in "
            + "the Alfresco repository are considered our internal implementation detail and might "
            + "change or even disappear in service packs and new versions without prior notice. "
            + "The following classes are making use of internal Alfresco classes:";
    
    @Override
    public String getHeader()
    {
        return HEADER;
    }

    @Override
    public Conflict.Type getConflictType()
    {
        return null;
    }
    
    @Override
    public void printVerboseOutput(String id, Set<Conflict> conflictSet)
    {
        //TODO to be discussed (https://issues.alfresco.com/jira/browse/ACS-81)
    }

    @Override
    public void print(String id, Set<Conflict> conflictSet)
    {
        String definingObject = conflictSet.iterator().next().getAmpResourceInConflict()
            .getDefiningObject();

        System.out.println(id + " in " + definingObject);
        System.out.println("using");
        System.out.println(joinWarResourceIds(conflictSet));
    }
}
