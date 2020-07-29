/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.BEAN_RESTRICTED_CLASS;
import static org.alfresco.ampalyser.analyser.service.PrintingService.printTable;

import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.alfresco.ampalyser.model.BeanResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BeanRestrictedClassConflictPrinter implements ConflictPrinter
{
    private static final String HEADER = "Found beans instantiating internal classes.\nThe "
        + "following beans instantiate classes from Alfresco or 3rd party libraries which are "
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
        String[][] data = new String[conflictSet.size() + 1][4];
        data[0][0] = "Extension Bean Resource ID";
        data[0][1] = "Extension Defining Object";
        data[0][2] = "Restricted Class";
        data[0][3] = "WAR Version";

        int row = 1;
        for (Conflict conflict : conflictSet)
        {
            data[row][0] = conflict.getAmpResourceInConflict().getId();
            data[row][1] = conflict.getAmpResourceInConflict().getDefiningObject();
            data[row][2] = ((BeanResource)conflict.getAmpResourceInConflict()).getBeanClass();
            data[row][3] = conflict.getAlfrescoVersion();
            row++;
        }

        printTable(data);
    }

    @Override
    public void print(Set<Conflict> conflictSet)
    {
        String[][]data = new String[conflictSet.size() + 1][2];
        data[0][0] = "Extension Bean Resource ID";
        data[0][1] = "Restricted Class";

        int row = 1;
        for (Conflict conflict : conflictSet)
        {
            data[row][0] = conflict.getAmpResourceInConflict().getId();
            data[row][1] = ((BeanResource)conflict.getAmpResourceInConflict()).getBeanClass();
            row++;
        }
        printTable(data);
    }
}
