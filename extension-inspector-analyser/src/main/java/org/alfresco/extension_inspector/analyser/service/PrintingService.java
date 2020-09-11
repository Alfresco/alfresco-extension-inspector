/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.extension_inspector.analyser.service;

import static org.springframework.shell.table.CellMatchers.column;

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
        tableBuilder.addInnerBorder(BorderStyle.oldschool);
        tableBuilder.addHeaderBorder(BorderStyle.oldschool);
        // Limit the size of the columns
        // On verbose output
        // Last column (tableModel.getColumnCount()-1) is always "Total conflicts" or "Total" (for report summary) - we'll skip it
        // Also skip "War Versions" column
        if(tableModel.getColumnCount() > 2)
        {
            for (int i = 0; i <= tableModel.getColumnCount() - 3; i++)
            {
                tableBuilder.on(column(i))
                    .addSizer((strings, i12, i1) -> new SizeConstraints.Extent(0, 70));
            }
        }

        System.out.println(tableBuilder.build().render(180));
    }
}
