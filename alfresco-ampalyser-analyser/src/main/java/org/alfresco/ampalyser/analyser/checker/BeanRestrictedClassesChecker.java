/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.alfresco.ampalyser.model.Resource.Type.ALFRESCO_PUBLIC_API;
import static org.alfresco.ampalyser.model.Resource.Type.BEAN;

import java.util.Set;
import java.util.stream.Stream;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.RestrictedBeanClassConflict;
import org.alfresco.ampalyser.analyser.service.ConfigService;
import org.alfresco.ampalyser.model.BeanResource;
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
public class BeanRestrictedClassesChecker implements Checker
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanRestrictedClassesChecker.class);

    private static final String ORG_ALFRESCO_PREFIX = "org.alfresco";

    @Autowired
    private ConfigService configService;

    @Override
    public Stream<Conflict> processInternal(final InventoryReport warInventory, final String alfrescoVersion)
    {
        final Set<String> blacklist = Stream
            .concat(
                // The list coming from the file the user provided
                configService.getBeanClassBlacklist().stream(),

                // By default, add the ALFRESCO_PUBLIC_API classes that we found in the war to the blacklist.
                warInventory
                    .getResources().getOrDefault(ALFRESCO_PUBLIC_API, emptyList())
                    .stream()
                    .map(Resource::getId))
            .collect(toUnmodifiableSet());

        return configService
            .getExtensionResources(BEAN)
            .stream()
            .filter(ampR -> (ampR instanceof BeanResource
                    && ((BeanResource) ampR).getBeanClass() != null)
                    && ((BeanResource) ampR).getBeanClass().startsWith(ORG_ALFRESCO_PREFIX))
            .filter(ampR -> !blacklist.contains(((BeanResource) ampR).getBeanClass())) // is this right?
            .map(ampR -> new RestrictedBeanClassConflict(ampR, null, alfrescoVersion));
    }

    @Override
    public boolean canProcess(final InventoryReport warInventory, final String alfrescoVersion)
    {
        return true;
    }
}
