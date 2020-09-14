/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.extension_inspector.analyser.checker;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.alfresco.extension_inspector.analyser.checker.Checker.isInAllowedList;
import static org.alfresco.extension_inspector.model.Resource.Type.ALFRESCO_PUBLIC_API;

import java.util.Set;
import java.util.stream.Stream;

import org.alfresco.extension_inspector.analyser.result.BeanRestrictedClassConflict;
import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.service.ConfigService;
import org.alfresco.extension_inspector.analyser.service.ExtensionResourceInfoService;
import org.alfresco.extension_inspector.model.InventoryReport;
import org.alfresco.extension_inspector.model.Resource;
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
        final Set<String> extensionClassesById = extensionResourceInfoService
            .retrieveClasspathElementsById().keySet();

        final Set<String> allowedList = configService.getInternalClassAllowedList();
        final Set<String> publicApis = // By default, add the ALFRESCO_PUBLIC_API classes that we found in the war to the publicApis.
            warInventory.getResources().getOrDefault(ALFRESCO_PUBLIC_API, emptySet())
                .stream()
                .map(Resource::getId)
                .collect(toUnmodifiableSet());

        return extensionResourceInfoService
            .retrieveBeansOfAlfrescoTypes()
            .stream()
            .filter(r -> !publicApis.contains(r.getBeanClass()))
            .filter(r -> !extensionClassesById
                .contains("/" + r.getBeanClass().replace(".", "/") + ".class"))
            .filter(r -> !isInAllowedList(
                "/" + r.getBeanClass().replace(".", "/") + ".class", allowedList))
            .map(r -> new BeanRestrictedClassConflict(r, alfrescoVersion));
    }

    @Override
    public boolean canProcess(final InventoryReport warInventory, final String alfrescoVersion)
    {
        return true;
    }
}
