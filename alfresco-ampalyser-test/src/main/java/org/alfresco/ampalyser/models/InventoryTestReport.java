/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

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
                return this.getResources().get(resourceType);
        }
}
