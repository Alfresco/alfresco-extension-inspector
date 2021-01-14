/*
 * Copyright 2021 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
