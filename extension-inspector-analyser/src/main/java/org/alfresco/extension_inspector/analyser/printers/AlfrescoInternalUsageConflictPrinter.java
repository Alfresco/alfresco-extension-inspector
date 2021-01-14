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

import static java.lang.String.format;
import static java.lang.String.join;
import static java.lang.String.valueOf;
import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.ALFRESCO_INTERNAL_USAGE;
import static org.alfresco.extension_inspector.analyser.service.PrintingService.printTable;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.alfresco.extension_inspector.analyser.result.AlfrescoInternalUsageConflict;
import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.store.WarInventoryReportStore;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AlfrescoInternalUsageConflictPrinter implements ConflictPrinter
{
    private static final String HEADER = "The following classes defined in the extension module are using internal repository classes:";
    private static final String DESCRIPTION =
        "These classes are considered an internal implementation detail of the ACS repository and might change or completely disappear between ACS versions and even between service packs."
            + lineSeparator()
            + "For a complete usage matrix, use the -verbose option of this tool.";
    private static final String EXTENSION_RESOURCE_ID = "Extension Resource using Alfresco Internal code";

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
    public String getDescription()
    {
        return DESCRIPTION;
    }
    
    @Override
    public String getSection()
    {
        return "Custom code using internal classes";
    }

    @Override
    public Conflict.Type getConflictType()
    {
        return ALFRESCO_INTERNAL_USAGE;
    }

    @Override
    public void printVerboseOutput(final Set<Conflict> conflictSet)
    {
        String[][] data =  conflictSet
            .stream()
            .collect(groupingBy(conflict -> conflict.getAmpResourceInConflict().getId(),
                TreeMap::new,
                toUnmodifiableSet()))
            .entrySet().stream()
            .map(entry -> List.of(
                entry.getKey()
                    .substring(1)
                    .replaceAll("/", ".")
                    .replace(".class", ""),
                entry.getValue().iterator().next().getAmpResourceInConflict().getDefiningObject(),
                join("\n\n", ((AlfrescoInternalUsageConflict)entry.getValue().iterator().next())
                    .getInvalidAlfrescoDependencies()),// Empty line between dependencies for output readability
                joinWarVersions(entry.getValue()),
                valueOf(entry.getValue().size())))
            .map(rowAsList -> rowAsList.toArray(new String[0]))
            .toArray(String[][]::new);

        data = ArrayUtils.insert(0, data, new String[][] {
            new String[] { EXTENSION_RESOURCE_ID, EXTENSION_DEFINING_OBJECT,
                INTERNAL_REPOSITORY_CLASSES,
                WAR_VERSION, TOTAL } });
        printTable(data);
    }

    @Override
    public void print(final Set<Conflict> conflictSet)
    {
        System.out.println(
            conflictSet
                .stream()
                .map(conflict -> format("\t%s", conflict.getAmpResourceInConflict().getId()
                    .substring(1)
                    .replaceAll("/", ".")
                    .replace(".class", "")))
                .distinct()
                .sorted()
                .collect(joining("\n")));
        System.out.println("Internal repository classes:");
        System.out.println(
            conflictSet
                .stream()
                .flatMap(conflict -> ((AlfrescoInternalUsageConflict)conflict).getInvalidAlfrescoDependencies()
                    .stream())
                .distinct()
                .sorted()
                .map(s -> format("\t%s", s))
                .collect(joining("\n")));
    }
}
