/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.WAR_LIBRARY_USAGE;

import java.util.Set;
import java.util.SortedSet;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WarLibraryUsageConflictPrinter implements ConflictPrinter
{
    private static final String HEADER =
        "Found 3rd party library usage! Although this is not an "
        + "immediate problem, all 3rd party libraries that come with the Alfresco "
        + "repository are considered our internal implementation detail. These "
        + "libraries will change or might even disappear in service packs without "
        + "notice.\nThe following classes are making use of 3rd party libraries:";

    @Autowired
    private WarInventoryReportStore store;

    @Override
    public SortedSet<String> retrieveAllKnownVersions()
    {
        return store.allKnownVersions();
    }

    @Override
    public String getHeader()
    {
        return HEADER;
    }

    @Override
    public Conflict.Type getConflictType()
    {
        return WAR_LIBRARY_USAGE;
    }

    @Override
    public void printVerboseOutput(final Set<Conflict> conflictSet)
    {
//        final String definingObject = conflictSet.iterator().next().getAmpResourceInConflict().getDefiningObject();
//        final String invalidDependencies = conflictSet
//            .stream()
//            .map(c -> (WarLibraryUsageConflict) c)
//            .flatMap(c -> c.getClassDependencies().stream())
//            .distinct()
//            .sorted()
//            .collect(joining(", "));
//
//        System.out.println(
//            "Extension resource " + (id.equals(definingObject) ? id : id + "@" + definingObject)
//                + " has invalid (3rd party) dependencies: " + invalidDependencies);
//        System.out.println("Conflicting with: " + joinWarVersions(conflictSet));
//        System.out.println();
    }

    @Override
    public void print(final Set<Conflict> conflictSet)
    {
//        final String definingObject = conflictSet.iterator().next().getAmpResourceInConflict().getDefiningObject();
//
//        System.out.println((id.equals(definingObject) ? id : id + "@" + definingObject));
//        System.out.println();
    }
}
