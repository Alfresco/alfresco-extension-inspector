/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.BEAN_OVERWRITE;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alfresco.ampalyser.analyser.result.Conflict;

public class BeanOverwriteConflictPrinter implements ConflictPrinter
{
    @Override
    public void print(Map<String, Set<Conflict>> conflicts, boolean verbose)
    {
        System.out.println("Found bean overwrites! Spring beans defined by Alfresco constitute\n"
            + "a fundamental building block of the repository and must not be\n"
            + "overwritten unless explicitly allowed. Found the following beans\n"
            + "overwriting default Alfresco functionality:");

        if (verbose)
        {
            conflicts.forEach(BeanOverwriteConflictPrinter::printVerboseOutput);
        }
        else
        {
            conflicts.forEach(BeanOverwriteConflictPrinter::print);

            System.out.println("(use option --verbose for version details)");
        }
    }

    @Override
    public Conflict.Type getConflictType()
    {
        return BEAN_OVERWRITE;
    }

    private static void print(String id, Set<Conflict> conflictSet)
    {
        String definingObjs = conflictSet
            .stream()
            .map(conflict -> conflict.getAmpResourceInConflict().getDefiningObject())
            .distinct()
            .collect(Collectors.joining(", "));

        System.out.println(id + " defined in " +definingObjs);
    }

    private static void printVerboseOutput(String id, Set<Conflict> conflictSet)
    {
        String versions = conflictSet
            .stream()
            .map(Conflict::getAlfrescoVersion)
            .collect(Collectors.joining(", "));

        print(id, conflictSet);
        System.out.println("Overwriting bean in " + versions);
        System.out.println();
    }
}
