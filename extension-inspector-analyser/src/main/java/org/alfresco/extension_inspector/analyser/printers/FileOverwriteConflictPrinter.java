/*
 * Copyright 2021 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfresco.extension_inspector.analyser.printers;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.FILE_OVERWRITE;
import static org.alfresco.extension_inspector.analyser.service.PrintingService.printTable;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.store.WarInventoryReportStore;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileOverwriteConflictPrinter implements ConflictPrinter
{
    private static final String HEADER = "The following files in the extension module conflict with resources in the ACS repository:";
    private static final String DESCRIPTION =
        "The module management tool will reject modules with file conflicts, making it impossible to install this module."
            + lineSeparator()
            + "It is possible that these conflicts only exist in specific ACS versions. Run this tool with the -verbose option to get a complete list of versions where each of these files has conflicts.";
    private static final String EXTENSION_RESOURCE_ID = "Extension Resource overwriting WAR resource";

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
        return "File conflicts";
    }

    @Override
    public Conflict.Type getConflictType()
    {
        return FILE_OVERWRITE;
    }

    @Override
    public void printVerboseOutput(Set<Conflict> conflictSet)
    {
        String[][] data =  conflictSet
            .stream()
            .collect(groupingBy(conflict -> conflict.getAmpResourceInConflict().getId(),
                TreeMap::new,
                toUnmodifiableSet()))
            .entrySet().stream()
            .map(entry -> List.of(
                entry.getKey(),
                joinWarVersions(entry.getValue()),
                valueOf(entry.getValue().size())))
            .map(rowAsList -> rowAsList.toArray(new String[0]))
            .toArray(String[][]::new);

        data = ArrayUtils.insert(0, data,
            new String[][] { new String[] { EXTENSION_RESOURCE_ID, WAR_VERSION, TOTAL } });
        printTable(data);
    }

    @Override
    public void print(Set<Conflict> conflictSet)
    {
        System.out.println(
            conflictSet
                .stream()
                .map(conflict -> format("\t%s",conflict.getAmpResourceInConflict().getId()))
                .distinct()
                .sorted()
                .collect(joining("\n")));
    }
}
