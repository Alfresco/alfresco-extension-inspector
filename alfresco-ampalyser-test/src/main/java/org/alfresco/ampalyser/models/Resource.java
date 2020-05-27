package org.alfresco.ampalyser.models;

public class Resource {
    public String type;
    public String id;
    public String definingObject;

    @Override
    public String toString()
    {
        return "Resource{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", definingObject='" + definingObject + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDefiningObject() {
        return definingObject;
    }

    public void setDefiningObject(String definingObject) {
        this.definingObject = definingObject;
    }
}
