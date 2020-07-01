/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.printers.ConflictPrinter.joinWarVersions;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.FILE_OVERWRITE;

import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.springframework.stereotype.Component;

@Component
public class FileOverwriteConflictPrinter implements ConflictPrinter
{
    @Override
    public void print(Map<String, Set<Conflict>> conflicts, boolean verbose)
    {
        if (conflicts == null || conflicts.isEmpty())
        {
            return;
        }
        
        System.out.println("Found resource conflicts! The following resources will conflict with\n"
            + "resources present in various Alfresco versions. It will not be\n"
            + "possible to install this AMP on these versions. (You can use the\n"
            + "option --target to limit this scan to specific Alfresco versions)");
        System.out.println();

        if (verbose)
        {
            conflicts.forEach(FileOverwriteConflictPrinter::printVerboseOutput);
        }
        else
        {
            conflicts
                .keySet()
                .forEach(System.out::println);

            System.out.println("(use option --verbose for version details)");
        }
    }
    
    @Override
    public Conflict.Type getConflictType()
    {
        return FILE_OVERWRITE;
    }
    
    private static void printVerboseOutput(String id, Set<Conflict> conflictSet)
    {
        String warResourceId = conflictSet.iterator().next().getWarResourceInConflict().getId();

        System.out.println(id + " (conflicting with " + warResourceId + ")");
        System.out.println("Conflicting with " + joinWarVersions(conflictSet));
        System.out.println();
    }
}
