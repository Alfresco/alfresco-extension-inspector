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
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.alfresco.ampalyser.model.Resource.Type.ALFRESCO_PUBLIC_API;
import static org.alfresco.ampalyser.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.StringUtils.stripEnd;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.BeanRestrictedClassConflict;
import org.alfresco.ampalyser.analyser.service.ConfigService;
import org.alfresco.ampalyser.analyser.service.ExtensionResourceInfoService;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private ConfigService configService;
    @Autowired
    private ExtensionResourceInfoService extensionResourceInfoService;

    @Override
    public Stream<Conflict> processInternal(final InventoryReport warInventory, final String alfrescoVersion)
    {
        final List<String> warJavaClasses = warInventory
            .getResources().getOrDefault(CLASSPATH_ELEMENT, emptyList())
            .stream()
            .filter(r -> r.getId().endsWith(".class"))
            .map(Resource::getId)
            .map(id -> replace(
                stripStart(
                    stripEnd(id, ".class"), 
                    "/"), 
                "/", "."))
            .collect(toUnmodifiableList());
        
        final Set<String> whitelist = Stream
            .concat(
                // The list coming from the file the user provided
                configService.getBeanClassWhitelist().stream(),

                // By default, add the ALFRESCO_PUBLIC_API classes that we found in the war to the whitelist.
                warInventory
                    .getResources().getOrDefault(ALFRESCO_PUBLIC_API, emptyList())
                    .stream()
                    .map(Resource::getId))
            .collect(toUnmodifiableSet());

        return extensionResourceInfoService
            .retrieveBeansOfAlfrescoTypes()
            .stream()
            .filter(r -> warJavaClasses.contains(r.getBeanClass()) && 
                !whitelist.contains(r.getBeanClass()))
            .map(r -> new BeanRestrictedClassConflict(r, alfrescoVersion));
    }

    @Override
    public boolean canProcess(final InventoryReport warInventory, final String alfrescoVersion)
    {
        return true;
    }
}
