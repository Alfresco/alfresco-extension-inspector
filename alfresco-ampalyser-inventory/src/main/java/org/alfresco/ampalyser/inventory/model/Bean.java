/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.model;

import java.util.Objects;

public class Bean
{
    private String name;
    private String location;

    public Bean(String name, String location)
    {
        this.name = name;
        this.location = location;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof Bean))
            return false;
        Bean bean = (Bean) o;
        return Objects.equals(name, bean.name) && Objects.equals(location, bean.location);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, location);
    }

    @Override
    public String toString()
    {
        return "{" + "name='" + name + '\'' + ", location='" + location + '\'' + '}';
    }
}
