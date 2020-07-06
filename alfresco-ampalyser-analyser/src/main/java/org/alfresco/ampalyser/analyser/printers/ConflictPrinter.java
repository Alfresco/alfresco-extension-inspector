/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alfresco.ampalyser.analyser.result.Conflict;

public interface ConflictPrinter
{
    default void print(Map<String, Set<Conflict>> conflicts, boolean verbose)
    {
        if (conflicts == null || conflicts.isEmpty())
        {
            return;
        }

        System.out.println(getHeader());
        System.out.println();

        if (verbose)
        {
            conflicts.forEach(this::printVerboseOutput);
        }
        else
        {
            conflicts.forEach(this::print);
        }

        System.out.println("-------------------------------------------------------------------");
        System.out.println();
    }
    
    String getHeader();

    Conflict.Type getConflictType();

    void printVerboseOutput(String id, Set<Conflict> conflictSet);
    
    void print(String id, Set<Conflict> conflictSet);
    
    static String joinWarVersions(Set<Conflict> conflictSet)
    {
        return conflictSet
            .stream()
            .map(Conflict::getAlfrescoVersion)
            .distinct()
            .sorted()
            .collect(Collectors.joining(", "));
    }

    static String joinWarResourceIds(Set<Conflict> conflictSet)
    {
        return conflictSet
            .stream()
            .map(conflict -> conflict.getWarResourceInConflict().getId())
            .distinct()
            .sorted()
            .collect(Collectors.joining("\n"));
    }

    static String joinExtensionDefiningObjs(Set<Conflict> conflictSet)
    {
        return conflictSet
            .stream()
            .map(conflict -> conflict.getAmpResourceInConflict().getDefiningObject())
            .distinct()
            .sorted()
            .collect(Collectors.joining(", "));
    }
}
