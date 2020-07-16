/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static java.lang.String.join;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.apache.maven.artifact.versioning.ComparableVersion;

public interface ConflictPrinter
{
    default void print(final Map<String, Set<Conflict>> conflicts, final boolean verbose)
    {
        if (isEmpty(conflicts))
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
    
    SortedSet<String> retrieveAllKnownVersions();
    
    String getHeader();

    Conflict.Type getConflictType();

    void printVerboseOutput(String id, Set<Conflict> conflictSet);
    
    void print(String id, Set<Conflict> conflictSet);
    
    default String joinWarVersions(Set<Conflict> conflictSet)
    {
        SortedSet<String> allKnownVersions = retrieveAllKnownVersions();
        
        SortedSet<String> conflictVersions = conflictSet
            .stream()
            .map(Conflict::getAlfrescoVersion)
            .collect(toCollection(() -> new TreeSet<>(comparing(ComparableVersion::new))));

        if (conflictVersions.equals(allKnownVersions))
        {
            return allKnownVersions.first() + " - " + allKnownVersions.last();
        }
        return groupRanges(conflictVersions)
            .stream()
            .map(l -> l.size() > 2 ? l.first() + " - " + l.last() : join(", ", l))
            .collect(joining(", "));
    }

    private List<SortedSet<String>> groupRanges(SortedSet<String> s)
    {
        List<SortedSet<String>> groups = new ArrayList<>();
        SortedSet<String> range = new TreeSet<>();

        List<String> bundledVersions = List.copyOf(retrieveAllKnownVersions());
        Iterator<String> conflictVersions = s.iterator();
        
        String version = conflictVersions.next();
        range.add(version);
        int index = bundledVersions.indexOf(version);
        while (conflictVersions.hasNext())
        {
            version = conflictVersions.next();
            index++;
            if (version.equals(bundledVersions.get(index)))
            {
                range.add(version);
            }
            else
            {
                groups.add(range);
                range = new TreeSet<>();

                range.add(version);
                index = bundledVersions.indexOf(version);
            }
            if (!conflictVersions.hasNext())
            {
                groups.add(range);
            }
        }

        return groups;
    }

    static String joinWarResourceIds(Set<Conflict> conflictSet)
    {
        return conflictSet
            .stream()
            .map(conflict -> conflict.getWarResourceInConflict().getId())
            .distinct()
            .sorted()
            .collect(joining("\n"));
    }

    static String joinExtensionDefiningObjs(Set<Conflict> conflictSet)
    {
        return conflictSet
            .stream()
            .map(conflict -> conflict.getAmpResourceInConflict().getDefiningObject())
            .distinct()
            .sorted()
            .collect(joining(", "));
    }
}
