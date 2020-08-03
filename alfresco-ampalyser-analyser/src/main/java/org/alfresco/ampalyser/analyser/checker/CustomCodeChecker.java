/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.Collections.emptySet;
import static java.util.Map.entry;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.alfresco.ampalyser.analyser.checker.Checker.isInAllowedList;
import static org.alfresco.ampalyser.model.Resource.Type.ALFRESCO_PUBLIC_API;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.CustomCodeConflict;
import org.alfresco.ampalyser.analyser.service.ConfigService;
import org.alfresco.ampalyser.analyser.service.ExtensionCodeAnalysisService;
import org.alfresco.ampalyser.analyser.service.ExtensionResourceInfoService;
import org.alfresco.ampalyser.model.AbstractResource;
import org.alfresco.ampalyser.model.AlfrescoPublicApiResource;
import org.alfresco.ampalyser.model.ClasspathElementResource;
import org.alfresco.ampalyser.model.InventoryReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A checker/analyser designed to find all the class dependencies of a class within the .amp
 * and to report a list of conflicts for each of those classes.
 *
 * Each class that is found containing invalid dependencies (see {@link CustomCodeConflict}) is reported
 * as the original .amp resource
 *
 * @author Lucian Tuca
 */
@Component
public class CustomCodeChecker implements Checker
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomCodeChecker.class);

    @Autowired
    private ConfigService configService;
    @Autowired
    private ExtensionResourceInfoService extensionResourceInfoService;
    @Autowired
    private ExtensionCodeAnalysisService extensionCodeAnalysisService;

    @Override
    public Stream<Conflict> processInternal(final InventoryReport warInventory, final String alfrescoVersion)
    {
        Set<String> allowedInternalClasses = configService.getInternalClassAllowedList();
        
        // Create a Map of the AlfrescoPublicApi with the class_id as the key and whether or not it is deprecated as the value
        final Map<String, Boolean> publicApis = warInventory
            .getResources().get(ALFRESCO_PUBLIC_API)
            .stream()
            .map(r -> (AlfrescoPublicApiResource) r)
            .collect(toUnmodifiableMap(
                r -> "/" + r.getId().replace(".", "/") + ".class",
                AlfrescoPublicApiResource::isDeprecated
            ));

        final Map<String, Set<ClasspathElementResource>> extensionClassesById =
            extensionResourceInfoService.retrieveClasspathElementsById();

        // go through the AMP dependencies and search for conflicts
        return extensionCodeAnalysisService
            .retrieveDependenciesPerClass()
            .entrySet()
            .stream()
            // map to (class_name -> {alfresco_dependencies_not_marked_as_PublicAPI})
            .map(e -> entry(
                e.getKey(),
                e.getValue()
                    .stream()
                    .filter(d -> d.startsWith("/org/alfresco/")) // It is an Alfresco class
                    .filter(d -> !extensionClassesById.containsKey(d)) // Not defined inside the AMP
                    .filter(d -> (!publicApis.containsKey(d) || publicApis.get(d)) && !isInAllowedList(d, allowedInternalClasses)) // Not PublicAPI or Deprecated_PublicAPI and not Allowed Internal Class
                    .collect(toUnmodifiableSet())
            ))
            .filter(e -> !e.getValue().isEmpty()) // strip entries without invalid dependencies
            .flatMap(e -> extensionClassesById
                .getOrDefault(e.getKey(), emptySet()) // a class can be provided by multiple jars
                .stream()
                .map(r -> new CustomCodeConflict(
                    r,
                    e.getValue(),
                    alfrescoVersion
                )));
    }

    @Override
    public boolean canProcess(final InventoryReport warInventory, final String alfrescoVersion)
    {
        return true;
    }
}
