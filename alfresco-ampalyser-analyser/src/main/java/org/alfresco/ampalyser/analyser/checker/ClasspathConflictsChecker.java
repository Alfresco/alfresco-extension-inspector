/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.Collections.emptyList;
import static org.alfresco.ampalyser.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.alfresco.ampalyser.analyser.result.ClasspathConflict;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.service.ConfigService;
import org.alfresco.ampalyser.analyser.service.ExtensionResourceInfoService;
import org.alfresco.ampalyser.model.ClasspathElementResource;
import org.alfresco.ampalyser.model.InventoryReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClasspathConflictsChecker implements Checker
{
    @Autowired
    private ConfigService configService;
    @Autowired
    private ExtensionResourceInfoService extensionResourceInfoService;

    @Override
    public Stream<Conflict> processInternal(final InventoryReport warInventory, final String alfrescoVersion)
    {
        final Map<String, Set<ClasspathElementResource>> elementsById =
            extensionResourceInfoService.retrieveClasspathElementsById();

        return warInventory
            .getResources().getOrDefault(CLASSPATH_ELEMENT, emptyList())
            .stream()
            .map(r -> (ClasspathElementResource) r)
            .filter(wr -> elementsById.containsKey(wr.getId()))
            .flatMap(wr -> elementsById
                .get(wr.getId())
                .stream()
                .map(r -> new ClasspathConflict(r, wr, alfrescoVersion)));
    }

    @Override
    public boolean canProcess(final InventoryReport warInventory, final String alfrescoVersion)
    {
        return !configService.getExtensionResources(CLASSPATH_ELEMENT).isEmpty() &&
               !isEmpty(warInventory.getResources().get(CLASSPATH_ELEMENT));
    }
}
