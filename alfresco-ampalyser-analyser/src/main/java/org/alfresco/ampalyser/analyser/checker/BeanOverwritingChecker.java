/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;
import static org.alfresco.ampalyser.model.Resource.Type.BEAN;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.alfresco.ampalyser.analyser.result.BeanOverwriteConflict;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.service.ConfigService;
import org.alfresco.ampalyser.analyser.service.ExtensionResourceInfoService;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Lucian Tuca
 */
@Component
public class BeanOverwritingChecker implements Checker
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanOverwritingChecker.class);

    @Autowired
    private ConfigService configService;
    @Autowired
    private ExtensionResourceInfoService extensionResourceInfoService;

    @Override
    public Stream<Conflict> processInternal(final InventoryReport warInventory, final String alfrescoVersion)
    {
        final Map<String, Set<Resource>> resourcesById = extensionResourceInfoService.retrieveBeanOverridesById();

        // Find a list of possible conflicts (there's no way to know for sure) for each amp bean resource
        return warInventory
            .getResources().getOrDefault(BEAN, emptyList())
            .stream()
            .filter(wr -> resourcesById.containsKey(wr.getId()))
            .flatMap(wr -> resourcesById
                .get(wr.getId())
                .stream()
                .map(r -> new BeanOverwriteConflict(r, wr, alfrescoVersion)));
    }

    @Override
    public boolean canProcess(final InventoryReport warInventory, final String alfrescoVersion)
    {
        return configService.getBeanOverrideWhitelist() != null;
    }
}
