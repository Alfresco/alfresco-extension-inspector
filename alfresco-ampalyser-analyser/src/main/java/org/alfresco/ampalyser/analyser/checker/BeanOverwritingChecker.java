/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.stream.Collectors.toList;
import static org.alfresco.ampalyser.model.Resource.Type.BEAN;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.result.BeanOverwriteConflict;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Lucian Tuca
 */
@Component
public class BeanOverwritingChecker implements Checker
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanOverwritingChecker.class);

    public static final String BEAN_OVERRIDING_WHITELIST = "BEAN_OVERRIDING_WHITELIST";

    @Override
    public List<Conflict> processInternal(Collection<Resource> ampResources,
        Collection<Resource> warResources, Map<String, Object> extraInfo)
    {
        Set<String> beanOverridingWhitelist = (Set<String>) extraInfo.get(BEAN_OVERRIDING_WHITELIST);
        Map<Resource, List<Resource>> beanOverridingLists = new HashMap<>();

        // Find a list of possible conflicts (there's no way to know for sure) for each amp bean resource
        for (Resource ampR : ampResources)
        {
            List<Resource> resourceInConflictWithAmpR = new LinkedList<>();
            for (Resource warR : warResources)
            {
                if (warR.getId().equals(ampR.getId()) && !beanOverridingWhitelist.contains(ampR.getId()))
                {
                    resourceInConflictWithAmpR.add(warR);
                }
            }
            if (!resourceInConflictWithAmpR.isEmpty())
            {
                beanOverridingLists.put(ampR, resourceInConflictWithAmpR);
            }
        }

        return beanOverridingLists.entrySet()
            .stream()
            .flatMap(ampR -> ampR.getValue().stream().map(warR -> new BeanOverwriteConflict(
                ampR.getKey(),
                warR,
                (String) extraInfo.get(ALFRESCO_VERSION))))
            .collect(toList());
    }

    @Override
    public boolean canProcessEntry(Collection<Resource> ampResources,
        Collection<Resource> warResources, Map<String, Object> extraInfo)
    {
        return ampResources.stream().filter(r -> BEAN != r.getType()).findAny().isEmpty()
            && warResources.stream().filter(r -> BEAN != r.getType()).findAny().isEmpty()
            && extraInfo != null
            && extraInfo.get(BEAN_OVERRIDING_WHITELIST) != null;
    }

    @Override
    public Resource.Type resourceType()
    {
        return BEAN;
    }
}
