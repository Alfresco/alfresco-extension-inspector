/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.model.Resource;

/**
 * Defines how a checker should work.
 *
 * @author Lucian Tuca
 */
public interface Checker
{
    String ALFRESCO_VERSION = "WAR_ALFRESCO_VERSION";

    default List<Conflict> process(Collection<Resource> ampResources, Collection<Resource> warResources, Map<String, Object> extraInfo)
    {
        if (canProcessEntry(ampResources, warResources, extraInfo))
        {
            return processInternal(ampResources, warResources, extraInfo);
        }
        return emptyList();
    }

    List<Conflict> processInternal(Collection<Resource> ampResources, Collection<Resource> warResources, Map<String, Object> extraInfo);

    boolean canProcessEntry(Collection<Resource> ampResources, Collection<Resource> warResources, Map<String, Object> extraInfo);

    Resource.Type resourceType();
}
