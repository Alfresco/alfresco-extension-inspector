/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.printers.ConflictPrinter.joinWarResourceDefiningObjs;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.CLASSPATH_CONFLICT;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClasspathConflictPrinter implements ConflictPrinter
{
    private static final String HEADER =
        "Found classpath conflicts! Although it might be possible to install "
            + "this extension, its behaviour is undefined.\nThe following resources in your "
            + "extension are in conflict with resources on the classpath in the Alfresco "
            + "repository:";

    private static final Set<String> CONFLICTING_EXTENSION_JARS_ALREADY_PRINTED = new HashSet<>();

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
        return CLASSPATH_CONFLICT;
    }

    @Override
    public void printVerboseOutput(String id, Set<Conflict> conflictSet)
    {
        final Conflict conflict = conflictSet.iterator().next();
        System.out.println(id + " in " + conflict.getAmpResourceInConflict().getDefiningObject()
            + " conflicts with " + joinWarResourceDefiningObjs(conflictSet));
        System.out.println("Conflicting with " + joinWarVersions(conflictSet));
        System.out.println();
    }

    @Override
    public void print(String id, Set<Conflict> conflictSet)
    {
        final Conflict conflict = conflictSet.iterator().next();
        final String ampResourceDefiningObject = conflict.getAmpResourceInConflict().getDefiningObject();

        // Keep an internal lists of conflicts per defining jar object.
        if (!CONFLICTING_EXTENSION_JARS_ALREADY_PRINTED.contains(ampResourceDefiningObject))
        {
            System.out.println(
                "Multiple resources in " + ampResourceDefiningObject + " conflicting with "
                    + joinWarResourceDefiningObjs(conflictSet));
            System.out.println();
            CONFLICTING_EXTENSION_JARS_ALREADY_PRINTED.add(ampResourceDefiningObject);
        }
    }
}
