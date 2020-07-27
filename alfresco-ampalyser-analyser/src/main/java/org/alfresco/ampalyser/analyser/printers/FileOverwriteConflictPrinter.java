/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.FILE_OVERWRITE;

import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dnl.utils.text.table.TextTable;
import dnl.utils.text.table.csv.CsvTableModel;

@Component
public class FileOverwriteConflictPrinter implements ConflictPrinter
{
    private static final String HEADER =
        "Found resource conflicts!\nThe following resources will conflict with "
            + "resources present in various Alfresco versions. It will not be "
            + "possible to install this extension on these versions. (You can use the "
            + "option --target to limit this scan to specific Alfresco versions)";

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
        return FILE_OVERWRITE;
    }

    @Override
    public void printVerboseOutput(Set<Conflict> conflictSet) throws IOException
    {
        StringBuilder csv = new StringBuilder();
        csv.append("Extension Resource ID,Extension Defining Object,WAR Resource ID,WAR Version").append(System.lineSeparator());
        for (Conflict conflict : conflictSet)
        {
            csv
                .append(conflict.getAmpResourceInConflict().getId()).append(",")
                .append(conflict.getAmpResourceInConflict().getDefiningObject()).append(",")
                .append(conflict.getWarResourceInConflict().getId()).append(",")
                .append(conflict.getAlfrescoVersion()).append(",").append(System.lineSeparator());
        }

        new TextTable(new CsvTableModel(csv.toString())).printTable();
        System.out.println();
    }

    @Override
    public void print(Set<Conflict> conflictSet)
    {
//        System.out.println(id);
//        System.out.println();
    }
}
