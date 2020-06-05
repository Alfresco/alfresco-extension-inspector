/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alfresco.ampalyser.models.InventoryTestReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class JsonInventoryParser {

    @Autowired
    private ObjectMapper objectMapper;

    public InventoryTestReport getInventoryReportFromJson(File jsonInventoryReport)
    {
        InventoryTestReport invReport = new InventoryTestReport();
        try
        {
            invReport = objectMapper.readValue(jsonInventoryReport, InventoryTestReport.class);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return invReport;
    }
}
