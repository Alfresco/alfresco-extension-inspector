/*
 * Copyright 2021 Alfresco Software, Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.alfresco.extension_inspector.analyser.printers;

import static java.lang.String.join;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.repeat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.store.WarInventoryReportStore;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ConflictPrinter
{
    Logger LOGGER = LoggerFactory.getLogger(ConflictPrinter.class);
    
    String EXTENSION_DEFINING_OBJECT = "Extension Defining Object";
    String THIRD_PARTY_DEPENDENCIES = "3rd Party Libraries";
    String INTERNAL_REPOSITORY_CLASSES = "Internal Repository Classes";
    String RESTRICTED_CLASS = "Restricted Class";
    String WAR_DEFINING_OBJECTS = "WAR Defining Objects";
    String WAR_VERSION = "WAR Versions";
    String TOTAL = "No. of WARs with this conflict";
    
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

        try
        {
            if (verbose)
            {
                System.out.println(getSection());
                System.out.println(repeat("-", getSection().length()));
                System.out.println(getDescription());
                System.out.println(getHeader());
                
                printVerboseOutput(allConflicts);
            }
            else
            {
                System.out.println(getSection());
                System.out.println(repeat("-", getSection().length()));
                System.out.println(getHeader());
                
                print(allConflicts);
                
                System.out.println(getDescription());
            }
        }
        catch (Exception e)
        {
            LOGGER.warn("Failed to print " + getSection(), e);
        }

        System.out.println();
    }
    
    SortedSet<String> retrieveAllKnownVersions();
    
    String getHeader();
    
    String getDescription();
    
    String getSection();

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
            return join(";", conflictVersions);
        }
        if (conflictVersions.equals(allKnownVersions))
        {
            return allKnownVersions.first() + "-" + allKnownVersions.last();
        }
        return groupRanges(conflictVersions)
            .stream()
            .map(s -> s.size() > 2 ? s.first() + "-" + s.last() : join(";", s))
            .collect(joining(";"));
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

    static String joinWarResourceDefiningObjs(String resourceId, Set<Conflict> conflictSet)
    {
        return conflictSet
            .stream()
            .filter(c -> c.getWarResourceInConflict().getId().equals(resourceId))
            .map(conflict -> conflict.getWarResourceInConflict().getDefiningObject())
            .distinct()
            .sorted()
            .collect(joining("\n\n"));// Empty line between defining objects for output readability
    }

    static String joinExtensionDefiningObjs(String resourceId, Set<Conflict> conflictSet)
    {
        return conflictSet
            .stream()
            .filter(c -> c.getAmpResourceInConflict().getId().equals(resourceId))
            .map(conflict -> conflict.getAmpResourceInConflict().getDefiningObject())
            .distinct()
            .sorted()
            .collect(joining("\n\n"));// Empty line between defining objects for output readability
    }
}
