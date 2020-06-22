/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.alfresco.ampalyser.model.Resource.Type.BEAN;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.result.BeanOverwriteConflict;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.model.InventoryReport;
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

    public static final String WHITELIST_BEAN_OVERRIDING = "WHITELIST_BEAN_OVERRIDING";

    @Override
    public List<Conflict> processInternal(final InventoryReport ampInventory, final InventoryReport warInventory, Map<String, Object> extraInfo)
    {
        Set<String> whitelist = (Set<String>) extraInfo.get(WHITELIST_BEAN_OVERRIDING);

        // Find a list of possible conflicts (there's no way to know for sure) for each amp bean resource
        return ampInventory.getResources().getOrDefault(BEAN, emptyList())
            .stream()
            .filter(ar -> !whitelist.contains(ar.getId()))
            .flatMap(ar -> warInventory.getResources().getOrDefault(BEAN, emptyList())
                .stream()
                .filter(wr -> wr.getId().equals(ar.getId()))
                .map(wr -> new BeanOverwriteConflict(ar, wr, (String) extraInfo.get(ALFRESCO_VERSION))))
            .collect(toUnmodifiableList());
    }

    @Override
    public boolean canProcess(InventoryReport ampInventory, InventoryReport warInventory, Map<String, Object> extraInfo)
    {
        return extraInfo != null
            && extraInfo.get(WHITELIST_BEAN_OVERRIDING) != null;
    }
}
