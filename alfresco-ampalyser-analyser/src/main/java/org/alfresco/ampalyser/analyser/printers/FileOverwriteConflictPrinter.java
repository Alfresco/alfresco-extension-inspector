/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.FILE_OVERWRITE;
import static org.alfresco.ampalyser.analyser.service.PrintingService.printTable;

import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileOverwriteConflictPrinter implements ConflictPrinter
{
    private static final String HEADER =
        "Found resource conflicts." + System.lineSeparator() + "The following resources will "
            + "conflict with resources used in various Alfresco versions, so you won't be able "
            + "to install this extension on these versions. Try using the "
            + "--target option to limit this scan to specific Alfresco versions.";

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
        String[][] data = new String[conflictSet.size() + 1][4];
        data[0][0] = "Extension Bean Resource ID";
        data[0][1] = "Extension Defining Object";
        data[0][2] = "WAR Version";

        int row = 1;
        for (Conflict conflict : conflictSet)
        {
            data[row][0] = conflict.getAmpResourceInConflict().getId();
            data[row][1] = conflict.getAmpResourceInConflict().getDefiningObject();
            data[row][2] = conflict.getWarResourceInConflict().getDefiningObject();
            data[row][3] = conflict.getAlfrescoVersion();
            row++;
        }

        printTable(data);
    }

    @Override
    public void print(Set<Conflict> conflictSet)
    {
        String[][] data = new String[conflictSet.size() + 1][1];
        data[0][0] = "Extension Resource ID overwriting WAR resources";

        int row = 1;
        for (Conflict conflict : conflictSet)
        {
            data[row++][0] = conflict.getAmpResourceInConflict().getId();
        }

        printTable(data);
    }
}
