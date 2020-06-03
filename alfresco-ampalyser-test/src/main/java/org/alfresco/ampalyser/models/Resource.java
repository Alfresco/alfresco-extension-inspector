package org.alfresco.ampalyser.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.alfresco.ampalyser.inventory.model.AbstractResource;

public class Resource extends AbstractResource
{
        private Boolean deprecated;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public Resource(@JsonProperty("type") Type type,
                        @JsonProperty("id") String id,
                        @JsonProperty("definingObject") String definingObject,
                        @JsonProperty("deprecated") Boolean deprecated)
        {
                super(type, id, definingObject);
                this.deprecated = deprecated;
        }

        @Override
        public String toString()
        {
                return "Resource{" + " type='" + this.getType().name() + '\'' + " id='" + id + '\'' + ", definingObject='" + definingObject + '\'' + '}';
        }

        public Boolean getDeprecated()
        {
                return deprecated;
        }

        public void setDeprecated(Boolean deprecated)
        {
                this.deprecated = deprecated;
        }
}
