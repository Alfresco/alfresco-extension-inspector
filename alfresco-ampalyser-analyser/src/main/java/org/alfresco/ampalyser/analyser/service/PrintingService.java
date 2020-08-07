/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.service;

import static org.springframework.shell.table.CellMatchers.column;
import static org.springframework.shell.table.CellMatchers.table;

import org.springframework.shell.table.AbsoluteWidthSizeConstraints;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.SizeConstraints;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

/**
 * @author Lucian Tuca
 */
public class PrintingService
{
    public static void printTable(String[][] data)
    {
        TableModel tableModel = new ArrayTableModel(data);
        TableBuilder tableBuilder = new TableBuilder(tableModel);
        tableBuilder.addInnerBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        // When table has only one column, don't limit its size
        // For multiple columns:
        // Last column (tableModel.getColumnCount()-1) is always "Total conflicts" or "Total" (for report summary) - we'll skip it
        // Also skip "War Versions" noOfColumns if present
        int noOfColumns = tableModel.getColumnCount() > 2 && tableModel
            .getValue(0, tableModel.getColumnCount() - 2).equals("WAR Versions") ?
            tableModel.getColumnCount() - 3 :
            tableModel.getColumnCount() - 2;
        // Limit the size of the remaining columns
        for (int i = 0; i <= noOfColumns; i++)
        {
            tableBuilder.on(column(i))
                .addSizer((strings, i12, i1) -> new SizeConstraints.Extent(0, 60));
        }

        System.out.println(tableBuilder.build().render(180));
        System.out.println();
    }
}
