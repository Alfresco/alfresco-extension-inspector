/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.service;

import static java.util.Comparator.comparing;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.printers.ConflictPrinter;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dnl.utils.text.table.TextTable;
import dnl.utils.text.table.csv.CsvTableModel;

@Service
public class AnalyserOutputService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyserService.class);

    @Autowired
    private ConfigService configService;
    @Autowired
    private List<ConflictPrinter> printers;
    @Autowired
    private WarInventoryReportStore store;

    public void print(final Map<Conflict.Type, Map<String, Set<Conflict>>> conflictPerTypeAndResourceId)
    {
        int conflictsTotal = 0;

        StringBuilder csv = new StringBuilder();
        csv.append("Type,Total").append(System.lineSeparator());

        for (Map.Entry<Conflict.Type, Map<String, Set<Conflict>>> perType : conflictPerTypeAndResourceId.entrySet())
        {
            int conflictsPerType = 0;
            for (Map.Entry<String, Set<Conflict>> perVersion : perType.getValue().entrySet())
            {
                conflictsPerType += perVersion.getValue().size();
            }
            csv.append(perType.getKey().toString()).append(",").append(conflictsPerType).append(System.lineSeparator());
            conflictsTotal += conflictsPerType;
        }

        System.out.println("================");
        System.out.println("REPORT SUMMARY");
        System.out.println("================");

        if (conflictsTotal > 0)
        {
            try
            {
                System.out.println("Across the provided target versions, the following number of conflicts have been found:");
                new TextTable(new CsvTableModel(csv.toString())).printTable();
                System.out.println();
            }
            catch (IOException e)
            {
                LOGGER.warn("Failed to summarize the output for the requested analysis.", e);
            }
        }
        else {
            System.out.println("Across the provided target versions, no conflicts have been found.");
        }


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
}
