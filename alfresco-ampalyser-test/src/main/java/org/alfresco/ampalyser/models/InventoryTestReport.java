package org.alfresco.ampalyser.models;

import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;

import java.util.List;
import java.util.Map;

public class InventoryTestReport extends InventoryReport
{
        public Resource getResource(Resource.Type resourceType, String resourceId)
        {
                List<Resource> items = this.getResources().get(resourceType);

                for (Resource resource : items)
                {
                        if (resource.getId().equals(resourceId))
                        {
                                return resource;
                        }
                }
                return null;
        }

        public List<Resource> getResources(Resource.Type resourceType)
        {
                List<Resource> items = this.getResources().get(resourceType);
                return items;
        }
}
