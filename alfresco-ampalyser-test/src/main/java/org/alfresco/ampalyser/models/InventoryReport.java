package org.alfresco.ampalyser.models;

import java.util.List;
import java.util.Map;

public class InventoryReport
{
        private String schemaVersion;
        private String alfrescoVersion;
        private Map<String, List<Resource>> resources;

        public Resource getResource(Resource.Type resourceType, String resourceId)
        {
                List<Resource> items = this.resources.get(resourceType.name());

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
                List<Resource> items = this.resources.get(resourceType.name());
                return items;
        }

        @Override
        public String toString()
        {
                return "InventoryReport{" + "schemaVersion='" + schemaVersion + '\'' + ", alfrescoVersion='" + alfrescoVersion + '\'' + ", resources='"
                        + resources + '\'' + '}';
        }

        public String getSchemaVersion()
        {
                return schemaVersion;
        }

        public void setSchemaVersion(String schemaVersion)
        {
                this.schemaVersion = schemaVersion;
        }

        public String getAlfrescoVersion()
        {
                return alfrescoVersion;
        }

        public void setAlfrescoVersion(String alfrescoVersion)
        {
                this.alfrescoVersion = alfrescoVersion;
        }

        public Map<String, List<Resource>> getResources()
        {
                return resources;
        }

        public void setResources(Map<String, List<Resource>> resources)
        {
                this.resources = resources;
        }
}
