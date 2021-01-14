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
import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.CLASSPATH_CONFLICT;
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
public class ClasspathConflictPrinter implements ConflictPrinter
{
    private static final String HEADER = "The following files and libraries in the extension module cause conflicts on the Java classpath:";
    private static final String DESCRIPTION =
        "Ambiguous resources on the Java classpath render the behaviour of the JVM undefined (see Java specification)."
            + lineSeparator()
            + "Although it might be possible that the repository can still start-up, you can expect erroneous behavior in certain situations. Problems of this kind are typically very hard to detect and trace back to their root cause."
            + lineSeparator()
            + "It is possible that these conflicts only exist in specific ACS versions. Run this tool with the -verbose option to get a complete list of versions where each of these files has conflicts.";

    private static final String EXTENSION_DEFINING_OBJECT = "Extension Classpath Resource Defining Object";
    private static final String EXTENSION_RESOURCE_ID = "Extension Classpath Resource";

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
        return "Classpath conflicts";
    }
    
    @Override
    public Conflict.Type getConflictType()
    {
        return CLASSPATH_CONFLICT;
    }

    @Override
    public void printVerboseOutput(Set<Conflict> conflictSet)
    {
        String[][] data =  conflictSet
            .stream()
            .collect(groupingBy(c -> c.getAmpResourceInConflict().getId() + "@" + c.getAmpResourceInConflict().getDefiningObject(),
                TreeMap::new,
                toUnmodifiableSet()))
            .values().stream()
            .map(conflicts -> {
                Conflict conflict = conflicts.iterator().next();
                return List.of(
                        conflict.getAmpResourceInConflict().getId(),
                        conflict.getAmpResourceInConflict().getDefiningObject(),
                        conflict.getWarResourceInConflict().getDefiningObject(),
                        joinWarVersions(conflicts), valueOf(conflicts.size()));
            })
            .map(rowAsList -> rowAsList.toArray(new String[0]))
            .toArray(String[][]::new);

        data = ArrayUtils.insert(0, data, new String[][] {
            new String[] { EXTENSION_RESOURCE_ID, EXTENSION_DEFINING_OBJECT, WAR_DEFINING_OBJECTS,
                WAR_VERSION, TOTAL } });
        
        printTable(data);
    }

    @Override
    public void print(Set<Conflict> conflictSet)
    {
        System.out.println(
            conflictSet
                .stream()
                .map(conflict -> format("\t%s",conflict.getAmpResourceInConflict().getDefiningObject()))
                .distinct()
                .sorted()
                .collect(joining("\n")));
    }
}
