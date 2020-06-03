package org.alfresco.ampalyser.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alfresco.ampalyser.models.InventoryReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

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
