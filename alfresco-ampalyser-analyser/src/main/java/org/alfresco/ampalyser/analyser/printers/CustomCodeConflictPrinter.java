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
        "Found usage of internal classes. Alfresco provides a Java API "
            + "that is clearly marked as @AlfrescoPublicAPI. Any other classes or interfaces in "
            + "the repository are considered our internal implementation detail and might "
            + "change or even disappear in service packs and new versions without prior notice. "
            + System.lineSeparator() + "The following classes use internal Alfresco classes:";

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
    public void printVerboseOutput(final Set<Conflict> conflictSet)
    {
        String[][] data = new String[conflictSet.size() + 1][4];
        data[0][0] = "Extension Resource ID";
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
        String[][] data = new String[conflictSet.size() + 1][1];
        data[0][0] = "Extension Resource ID using Custom Code";

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
