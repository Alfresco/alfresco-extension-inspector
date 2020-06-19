/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.alfresco.ampalyser.model.Resource.Type.BEAN;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.RestrictedBeanClassConflict;
import org.alfresco.ampalyser.model.BeanResource;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Lucian Tuca
 */
@Component
public class BeanRestrictedClassesChecker implements Checker
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanRestrictedClassesChecker.class);

    private static final String ORG_ALFRESCO_PREFIX = "org.alfresco";
    public static final String WHITELIST_BEAN_RESTRICTED_CLASSES = "WHITELIST_BEAN_RESTRICTED_CLASSES";

    @Override
    public List<Conflict> processInternal(InventoryReport ampInventory, InventoryReport warInventory, Map<String, Object> extraInfo)
    {
        // The list coming from the file the user provided
        Set<String> whitelist = (Set<String>) extraInfo.get(WHITELIST_BEAN_RESTRICTED_CLASSES);

        // By default, add the ALFRESCO_PUBLIC_API classes that we found in the war to the complete whitelist.
        Set<String> completeWhitelist = new HashSet<>(whitelist);
        completeWhitelist.addAll(
            warInventory.getResources().get(Resource.Type.ALFRESCO_PUBLIC_API)
                .stream()
                .map(Resource::getId)
                .collect(toList()));

        return ampInventory.getResources().get(BEAN).stream()
                .filter(ampR -> (ampR instanceof BeanResource
                    && ((BeanResource) ampR).getBeanClass() != null)
                    && ((BeanResource) ampR).getBeanClass().startsWith(ORG_ALFRESCO_PREFIX))
                .filter(ampR -> completeWhitelist
                            .stream()
                            .noneMatch(entry -> entry.contains(((BeanResource) ampR).getBeanClass())))
                .map(ampR -> new RestrictedBeanClassConflict(ampR, null, (String) extraInfo.get(ALFRESCO_VERSION)))
            .collect(toUnmodifiableList());
    }


    @Override
    public boolean canProcess(InventoryReport ampInventory, InventoryReport warInventory, Map<String, Object> extraInfo)
    {
        return extraInfo != null;
    }
}
