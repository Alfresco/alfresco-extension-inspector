/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.WAR_LIBRARY_USAGE;
import static org.alfresco.ampalyser.analyser.service.PrintingService.printTable;

import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.WarLibraryUsageConflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WarLibraryUsageConflictPrinter implements ConflictPrinter
{
    private static final String HEADER =
        "Found 3rd party library usage. Although this isn't an immediate problem, all 3rd party "
            + "libraries that are delivered with the repository are considered as our internal "
            + "implementation detail. These libraries will change or may be removed in future "
            + "service packs without notice." + System.lineSeparator()
            + "The following classes use 3rd party libraries:";

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
        return WAR_LIBRARY_USAGE;
    }

    @Override
    public void printVerboseOutput(final Set<Conflict> conflictSet) throws IOException
    {
        String[][] data = new String[conflictSet.size() + 1][4];
        data[0][0] = "Extension Bean Resource ID";
        data[0][1] = "Extension Defining Object";
        data[0][2] = "WAR Version";
        data[0][3] = "Invalid 3rd Party Dependencies";

        int row = 0;
        for (Conflict conflict : conflictSet)
        {
            row++;
            data[row][0] = conflict.getAmpResourceInConflict().getId();
            data[row][1] = conflict.getAmpResourceInConflict().getDefiningObject();
            data[row][2] = conflict.getAlfrescoVersion();
            data[row][3] = String.join(";",((WarLibraryUsageConflict) conflict).getClassDependencies());
        }

        // TODO: Enable when we eliminate the false positives. Outpur is larger than console buffer.
        // printTable(data);
    }

    @Override
    public void print(final Set<Conflict> conflictSet)
    {
        String[][] data = new String[conflictSet.size() + 1][1];
        data[0][0] = "Extension Resource ID using 3rd Party library code";

        int row = 1;
        for (Conflict conflict : conflictSet)
        {
            final String id = conflict.getAmpResourceInConflict().getId();;
            final String definingObject = conflictSet.iterator().next().getAmpResourceInConflict().getDefiningObject();
            data[row][0] = id.equals(definingObject) ? id : id + "@" + definingObject;

            row++;
        }

        printTable(data);
    }
}
