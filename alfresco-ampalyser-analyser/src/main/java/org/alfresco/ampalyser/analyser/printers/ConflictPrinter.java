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
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ConflictPrinter
{
    Logger LOGGER = LoggerFactory.getLogger(ConflictPrinter.class);

    default void print(final Map<String, Set<Conflict>> conflicts, final boolean verbose)
    {
        if (isEmpty(conflicts))
        {
            return;
        }

        // TODO: We can even sort the conflicts based on the war version for a prettier output?
        final Set<Conflict> allConflicts = conflicts.values()
            .stream()
            .flatMap(Set::stream)
            .collect(toSet());


        System.out.println("===============================");
        System.out.println(getConflictType() + " CONFLICTS");
        System.out.println("===============================");
        System.out.println(getHeader());
        System.out.println();

        try
        {
            if (verbose)
            {
                printVerboseOutput(allConflicts);
            }
            else
            {
                print(allConflicts);
            }
        }
        catch (Exception e)
        {
            LOGGER.warn("Failed to print " + getConflictType() + " conflicts!", e);
        }

        System.out.println("-------------------------------------------------------------------");
        System.out.println();
    }
    
    SortedSet<String> retrieveAllKnownVersions();
    
    String getHeader();

    Conflict.Type getConflictType();

    void printVerboseOutput(Set<Conflict> conflictSet) throws IOException;
    
    void print(Set<Conflict> conflictSet);
    
    default String joinWarVersions(Set<Conflict> conflictSet)
    {
        SortedSet<String> allKnownVersions = retrieveAllKnownVersions();
        
        SortedSet<String> conflictVersions = conflictSet
            .stream()
            .map(Conflict::getAlfrescoVersion)
            .collect(toCollection(() -> new TreeSet<>(comparing(ComparableVersion::new))));

        if (conflictVersions.isEmpty())
        {
            return "";
        }
        if (conflictVersions.size() <= 2)
        {
            return join(", ", conflictVersions);
        }
        if (conflictVersions.equals(allKnownVersions))
        {
            return allKnownVersions.first() + " - " + allKnownVersions.last();
        }
        return groupRanges(conflictVersions)
            .stream()
            .map(s -> s.size() > 2 ? s.first() + " - " + s.last() : join(", ", s))
            .collect(joining(", "));
    }

    /**
     * Processes a given {@link SortedSet} and creates groups of consecutive Alfresco versions.
     * Two versions are consecutive if they follow each other continuously in the `allKnownVersions`
     * {@link SortedSet} provided by {@link WarInventoryReportStore}.
     *
     * @param versions A set of Alfresco versions
     * @return A {@link List} containing groups of consecutive Alfresco versions
     */
    private List<SortedSet<String>> groupRanges(SortedSet<String> versions)
    {
        List<SortedSet<String>> groups = new ArrayList<>();
        SortedSet<String> range = new TreeSet<>();

        List<String> bundledVersions = List.copyOf(retrieveAllKnownVersions());
        Iterator<String> conflictVersions = versions.iterator();

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

    static String joinWarResourceDefiningObjs(Set<Conflict> conflictSet)
    {
        return conflictSet
            .stream()
            .map(conflict -> conflict.getWarResourceInConflict().getDefiningObject())
            .distinct()
            .sorted()
            .collect(joining(", "));
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
