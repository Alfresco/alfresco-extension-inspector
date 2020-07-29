/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.service;

import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
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
        System.out.println(tableBuilder.build().render(255));
        System.out.println();
    }
}
