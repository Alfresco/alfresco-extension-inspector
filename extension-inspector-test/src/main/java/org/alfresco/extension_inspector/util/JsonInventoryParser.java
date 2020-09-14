/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.extension_inspector.util;

import java.io.File;
import java.io.IOException;

import org.alfresco.extension_inspector.model.InventoryReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonInventoryParser {

    @Autowired
    private ObjectMapper objectMapper;

    public InventoryReport getInventoryReportFromJson(File jsonInventoryReport)
    {
        InventoryReport invReport = new InventoryReport();
        try
        {
            invReport = objectMapper.readValue(jsonInventoryReport, InventoryReport.class);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return invReport;
    }
}
