package org.alfresco.ampalyser.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alfresco.ampalyser.models.InventoryReport;
import org.alfresco.ampalyser.models.Resource;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonInventoryParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static InventoryReport getInventoryReportFromJson(File jsonInventoryReport)
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

    public static Resource getResource(String resourceType, String resourceId, File jsonInventoryReport){
        InventoryReport invReport = getInventoryReportFromJson(jsonInventoryReport);
        List<Resource> items = invReport.getResources().get(resourceType);;

        for(Resource resource: items){
            if(resource.id.equals(resourceId)){
                return resource;
            }
        }
        return  null;
    }

    public static List<Resource> getResources(String resourceType, File jsonInventoryReport){
        InventoryReport invReport = getInventoryReportFromJson(jsonInventoryReport);
        List<Resource> items = invReport.getResources().get(resourceType);
        return items;
    }

}
