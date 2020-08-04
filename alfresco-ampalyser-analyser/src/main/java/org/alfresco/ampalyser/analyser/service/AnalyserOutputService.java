/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.service;

import static java.util.Comparator.comparing;
import static org.alfresco.ampalyser.analyser.service.PrintingService.printTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.printers.ConflictPrinter;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyserOutputService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyserService.class);

    @Autowired
    private ConfigService configService;
    @Autowired
    private List<ConflictPrinter> printers;

    public void print(final Map<Conflict.Type, Map<String, Set<Conflict>>> conflictPerTypeAndResourceId)
    {
        printSummary(conflictPerTypeAndResourceId);

        printers
            .stream()
            .sorted(comparing(ConflictPrinter::getConflictType))
            .forEach(p -> p.print(
                conflictPerTypeAndResourceId.get(p.getConflictType()),
                configService.isVerboseOutput()
            ));

        if (!configService.isVerboseOutput())
        {
            System.out.println("(use option --verbose for version details)");
        }
    }

    private void printSummary(Map<Conflict.Type, Map<String, Set<Conflict>>> conflictPerTypeAndResourceId)
    {
        String[][] data = new String[2 + conflictPerTypeAndResourceId.size()][2];
        data[0][0] = "REPORT SUMMARY";
        data[1][0] = "Type";
        data[1][1] = "Total";

        int row = 2;

        int conflictsTotal = 0;
        for (Map.Entry<Conflict.Type, Map<String, Set<Conflict>>> perType : conflictPerTypeAndResourceId.entrySet())
        {
            int conflictsPerType = 0;
            for (Map.Entry<String, Set<Conflict>> perVersion : perType.getValue().entrySet())
            {
                conflictsPerType += perVersion.getValue().size();
            }

            data[row][0] = perType.getKey().toString();
            data[row][1] = String.valueOf(conflictsPerType);
            row++;

            conflictsTotal += conflictsPerType;
        }

        if (conflictsTotal > 0)
        {
            System.out.println("Across the provided target versions, the following number of conflicts have been found:");
            printTable(data);
        }
        else {
            System.out.println("Across the provided target versions, no conflicts have been found.");
        }
    }
}
