/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.BEAN_OVERWRITE;
import static org.alfresco.ampalyser.analyser.service.PrintingService.printTable;

import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BeanOverwriteConflictPrinter implements ConflictPrinter
{
    private static final String HEADER =
        "Found bean overwrites! Spring beans defined by Alfresco constitute "
        + "a fundamental building block of the repository and must not be "
        + "overwritten unless explicitly allowed.\nThe following beans "
        + "are overwriting default Alfresco functionality:";

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
        return BEAN_OVERWRITE;
    }

    @Override
    public void printVerboseOutput(Set<Conflict> conflictSet) throws IOException
    {
        String[][] data = new String[conflictSet.size() + 1][3];
        data[0][0] = "Extension Bean Resource ID";
        data[0][1] = "Extension Defining Object";
        data[0][2] = "WAR Version";

        int row = 1;
        for (Conflict conflict : conflictSet)
        {
            data[row][0] = conflict.getAmpResourceInConflict().getId();
            data[row][1] = conflict.getAmpResourceInConflict().getDefiningObject();
            data[row][2] = conflict.getAlfrescoVersion();
            row++;
        }

        printTable(data);
    }

    @Override
    public void print(Set<Conflict> conflictSet)
    {
        String[][] data = new String[conflictSet.size() + 1][3];
        data[0][0] = "Extension Bean Resource ID";
        data[0][1] = "Extension Defining Objects";
        data[0][2] = "WAR Defining object";

        int row = 1;
        for (Conflict conflict : conflictSet)
        {
            data[row][0] = conflict.getAmpResourceInConflict().getId();
            data[row][1] = ConflictPrinter.joinExtensionDefiningObjs(conflict.getAmpResourceInConflict().getId(), conflictSet);
            data[row][2] = conflict.getWarResourceInConflict().getDefiningObject();
            row++;
        }
        printTable(data);
    }
}
