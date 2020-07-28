/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static java.util.stream.Collectors.toSet;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.CLASSPATH_CONFLICT;
import static org.alfresco.ampalyser.analyser.service.PrintingService.printTable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClasspathConflictPrinter implements ConflictPrinter
{
    private static final String HEADER =
        "Found classpath conflicts! Although it might be possible to install "
            + "this extension, its behaviour is undefined.\nThe following resources in your "
            + "extension are in conflict with resources on the classpath in the Alfresco "
            + "repository:";

    private static final Set<String> CONFLICTING_EXTENSION_JARS_ALREADY_PRINTED = new HashSet<>();

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
        return CLASSPATH_CONFLICT;
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

        String[][] data = new String[conflictSet.stream().map(el -> el.getAmpResourceInConflict().getDefiningObject()).collect(toSet()).size() + 1][3];
        data[0][0] = "Extension Bean Resource Defining Object";
        data[0][1] = "WAR Defining Object";

        int row = 1;
        for (Conflict conflict : conflictSet)
        {
            final String extDefObj = conflict.getAmpResourceInConflict().getDefiningObject();
            if (!CONFLICTING_EXTENSION_JARS_ALREADY_PRINTED.contains(extDefObj))
            {
                data[row][0] = extDefObj;
                data[row][1] = ConflictPrinter.joinWarResourceDefiningObjs(conflictSet); // TODO: I'm not sure this is ok
                CONFLICTING_EXTENSION_JARS_ALREADY_PRINTED.add(extDefObj);
                row++;
            }
        }



        printTable(data);
    }
}
