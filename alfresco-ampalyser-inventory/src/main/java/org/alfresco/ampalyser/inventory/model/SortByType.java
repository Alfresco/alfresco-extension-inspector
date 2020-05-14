/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.model;

import static org.alfresco.ampalyser.inventory.model.Resource.*;

import java.util.Comparator;

class SortByType implements Comparator<Type>
{
    public int compare(Type a, Type b)
    {
        return a.name().compareTo(b.name());
    }
}
