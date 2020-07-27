/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.BEAN_RESTRICTED_CLASS;

import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.alfresco.ampalyser.model.BeanResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dnl.utils.text.table.TextTable;
import dnl.utils.text.table.csv.CsvTableModel;

@Component
public class BeanRestrictedClassConflictPrinter implements ConflictPrinter
{
    private static final String HEADER = "Found beans for restricted classes!\nThe following beans "
        + "instantiate classes from Alfresco or 3rd party libraries which are "
        + "not meant to be instantiated by custom beans:";

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
        return BEAN_RESTRICTED_CLASS;
    }

    @Override
    public void printVerboseOutput(Set<Conflict> conflictSet) throws IOException
    {
        StringBuilder csv = new StringBuilder();
        csv.append("Bean Resource Id,Restricted Class,Extension Defining Object,WAR Version").append(System.lineSeparator());
        for (Conflict conflict : conflictSet)
        {
            csv
                .append(conflict.getAmpResourceInConflict().getId()).append(",")
                .append(((BeanResource) conflict.getAmpResourceInConflict()).getBeanClass()).append(",")
                .append(conflict.getAmpResourceInConflict().getDefiningObject()).append(",")
                .append(conflict.getAlfrescoVersion()).append(System.lineSeparator());
        }

        new TextTable(new CsvTableModel(csv.toString())).printTable();
        System.out.println();
    }

    @Override
    public void print(Set<Conflict> conflictSet)
    {
//        Conflict conflict = conflictSet.iterator().next();
//        BeanResource resource = (BeanResource) conflict.getAmpResourceInConflict();
//
//        System.out.println(id + " instantiates " + resource.getBeanClass());
//        System.out.println();
    }
}
