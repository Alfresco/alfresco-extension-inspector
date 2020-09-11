/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.extension_inspector.analyser.printers;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.alfresco.extension_inspector.analyser.result.Conflict.Type.BEAN_RESTRICTED_CLASS;
import static org.alfresco.extension_inspector.analyser.service.PrintingService.printTable;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.store.WarInventoryReportStore;
import org.alfresco.extension_inspector.model.BeanResource;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BeanRestrictedClassConflictPrinter implements ConflictPrinter
{
    private static final String HEADER = "The following Spring beans defined in the extension module instantiate internal classes:";
    private static final String DESCRIPTION = "These classes are considered an internal implementation detail of the ACS repository and do not constitute a supported extension point. They might change or completely disappear between ACS versions and even in service packs.";
    private static final String EXTENSION_RESOURCE_ID = "Extension Bean ID instantiating Restricted Class";

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
        return "Beans instantiating internal classes";
    }

    @Override
    public Conflict.Type getConflictType()
    {
        return BEAN_RESTRICTED_CLASS;
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
                entry.getValue().iterator().next().getAmpResourceInConflict().getDefiningObject(),
                ((BeanResource)entry.getValue().iterator().next().getAmpResourceInConflict()).getBeanClass(),
                joinWarVersions(entry.getValue()),
                valueOf(entry.getValue().size())))
            .map(rowAsList -> rowAsList.toArray(new String[0]))
            .toArray(String[][]::new);

        data = ArrayUtils.insert(0, data, new String[][] {
            new String[] { EXTENSION_RESOURCE_ID, EXTENSION_DEFINING_OBJECT, RESTRICTED_CLASS,
                WAR_VERSION, TOTAL } });
        
        printTable(data);
    }

    @Override
    public void print(Set<Conflict> conflictSet)
    {
        System.out.println(
            conflictSet
                .stream()
                .map(conflict -> format("\t%s",conflict.getAmpResourceInConflict().getId() + " (class=" 
                    + ((BeanResource) conflict.getAmpResourceInConflict()).getBeanClass() + ")"))
                .distinct()
                .sorted()
                .collect(joining("\n")));
    }
}
