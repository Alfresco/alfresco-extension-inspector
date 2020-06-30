/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.CLASSPATH_CONFLICT;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alfresco.ampalyser.analyser.result.Conflict;

public class ClasspathConflictPrinter implements ConflictPrinter
{
    @Override
    public void print(Map<String, Set<Conflict>> conflicts, boolean verbose)
    {
        System.out.println("Found classpath conflicts! Although it might be possible to install\n"
            + "this AMP, its behaviour is undefined. The following resource in your\n"
            + "AMP are in conflict with resources on the classpath in the Alfresco\n"
            + "repository:");

        if (verbose)
        {
            conflicts.forEach(ClasspathConflictPrinter::printVerboseOutput);
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
        return CLASSPATH_CONFLICT;
    }
    
    private static void printVerboseOutput(String id, Set<Conflict> conflictSet)
    {
        String versions = conflictSet
            .stream()
            .map(Conflict::getAlfrescoVersion)
            .collect(Collectors.joining(", "));

        System.out.println(id);
        System.out.println("Conflicting with " + versions);
        System.out.println();
    }
}
