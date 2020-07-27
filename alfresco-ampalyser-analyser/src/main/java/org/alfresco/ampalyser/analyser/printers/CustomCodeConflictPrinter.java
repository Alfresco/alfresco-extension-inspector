/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.CUSTOM_CODE;
import static org.alfresco.ampalyser.analyser.service.PrintingService.printTable;

import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.CustomCodeConflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CustomCodeConflictPrinter implements ConflictPrinter
{
    private static final String HEADER =
        "Found usage of internal Alfresco classes! Alfresco provides a Java API "
            + "that is clearly marked as @AlfrescoPublicAPI. Any other classes or interfaces in "
            + "the Alfresco repository are considered our internal implementation detail and might "
            + "change or even disappear in service packs and new versions without prior notice. "
            + "\nThe following classes are making use of internal Alfresco classes:";

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
        return CUSTOM_CODE;
    }

    @Override
    public void printVerboseOutput(final Set<Conflict> conflictSet) throws IOException
    {
        String[][] data = new String[conflictSet.size() + 1][4];
        data[0][0] = "Extension Bean Resource ID";
        data[0][1] = "Extension Defining Object";
        data[0][2] = "WAR Version";
        data[0][3] = "Invalid Dependencies";

        int row = 1;
        for (Conflict conflict : conflictSet)
        {
            data[row][0] = conflict.getAmpResourceInConflict().getId();
            data[row][1] = conflict.getAmpResourceInConflict().getDefiningObject();
            data[row][2] = conflict.getAlfrescoVersion();
            data[row][2] = String.join(";", ((CustomCodeConflict)conflict).getInvalidAlfrescoDependencies());
            row++;
        }

        printTable(data);
    }

    @Override
    public void print(final Set<Conflict> conflictSet)
    {
//        final String definingObject = conflictSet.iterator().next().getAmpResourceInConflict().getDefiningObject();
//
//        System.out.println((id.equals(definingObject) ? id : id + "@" + definingObject));
//        System.out.println();
    }
}
