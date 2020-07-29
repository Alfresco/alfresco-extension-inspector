/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.printers;

import static java.util.stream.Collectors.joining;
import static org.alfresco.ampalyser.analyser.printers.ConflictPrinter.joinWarVersions;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.MISSING_DEPENDENCY;

import java.util.Set;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.MissingDependencyConflict;
import org.springframework.stereotype.Component;

@Component
public class MissingDependencyConflictPrinter implements ConflictPrinter
{
    private static final String HEADER =
        "Found missing dependencies!\n" +
        "The following classes have dependencies not provided by the WAR:";

    @Override
    public String getHeader()
    {
        return HEADER;
    }

    @Override
    public Conflict.Type getConflictType()
    {
        return MISSING_DEPENDENCY;
    }

    @Override
    public void printVerboseOutput(String id, Set<Conflict> conflictSet)
    {
        final String definingObject = conflictSet.iterator().next().getAmpResourceInConflict().getDefiningObject();
        final String invalidDependencies = conflictSet
            .stream()
            .map(c -> (MissingDependencyConflict) c)
            .flatMap(c -> c.getUnsatisfiedDependencies().stream())
            .distinct()
            .sorted()
            .collect(joining(", "));

        System.out.println(
            "Extension resource " + (id.equals(definingObject) ? id : id + "@" + definingObject)
            + " has unsatisfied dependencies: " + invalidDependencies);
        System.out.println("Conflicting with: " + joinWarVersions(conflictSet));
        System.out.println();
    }

    @Override
    public void print(String id, Set<Conflict> conflictSet)
    {
        final String definingObject = conflictSet.iterator().next().getAmpResourceInConflict().getDefiningObject();

        System.out.println((id.equals(definingObject) ? id : id + "@" + definingObject));
        System.out.println();
    }
}
