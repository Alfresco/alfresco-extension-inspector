/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.alfresco.ampalyser.model.Resource.Type.ALFRESCO_PUBLIC_API;
import static org.alfresco.ampalyser.model.Resource.Type.FILE;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.CustomCodeConflict;
import org.alfresco.ampalyser.analyser.service.ConfigService;
import org.alfresco.ampalyser.analyser.service.ExtensionResourceInfoService;
import org.alfresco.ampalyser.model.AlfrescoPublicApiResource;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
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

    @Override
    public Stream<Conflict> processInternal(final InventoryReport warInventory, final String alfrescoVersion)
    {
        final Map<String, Set<String>> dependenciesPerClass = extensionResourceInfoService.retrieveDependenciesPerClass();

        // Create a Map of the AlfrescoPublicApi with the class/id as the key and whether or not it is deprecated as the value
        final Map<String, Boolean> warPublicApis = warInventory
            .getResources().get(ALFRESCO_PUBLIC_API)
            .stream()
            .map(r -> (AlfrescoPublicApiResource) r)
            .collect(toUnmodifiableMap(Resource::getId, AlfrescoPublicApiResource::isDeprecated));

        return dependenciesPerClass
            .entrySet()
            .stream()
            .map(e -> new SimpleEntry<>(
                e.getKey(),
                e.getValue()
                 .stream()
                 .filter(c -> isInvalidAlfrescoDependency(c, warPublicApis, dependenciesPerClass.keySet()))
                 .collect(toUnmodifiableSet())
            ))
            .filter(e -> !e.getValue().isEmpty())
            .map(e -> new CustomCodeConflict(
                findAmpResourceForJarClass(e.getKey(), configService.getExtensionResources(FILE)),
                null,
                e.getValue(),
                alfrescoVersion
            ));
    }

    /**
     * For a given class this method decides whether or not this class represents and invalid Alfresco dependency
     * by checking the following conditions:
     *
     * * It is an Alfresco class
     * * It is not marked as AlfrescoPublicAPI
     * * It is marked as AlfrescoPublicAPI but is Deprecated
     *
     * @param clazz
     * @param publicApis
     * @param extensionClasses
     * @return whether or not this class is an invalid Alfresco dependency
     */
    private static boolean isInvalidAlfrescoDependency(final String clazz,
        final Map<String, Boolean> publicApis,
        final Set<String> extensionClasses)
    {
        return clazz.startsWith("org.alfresco") &&
               !extensionClasses.contains(clazz) &&
               (!publicApis.containsKey(clazz) ||
                (publicApis.containsKey(clazz) && publicApis.get(clazz)));
    }

    /**
     * Given a java class name (found in the .jar lib of the .amp) this method find the corresponding .amp {@link org.alfresco.ampalyser.model.FileResource}
     *
     * @param jarClass      the Java class name that was found in the .jar
     * @param fileResources the extension file resources to look in
     * @return the {@link org.alfresco.ampalyser.model.FileResource} coressponding to the provided class name
     */
    private static Resource findAmpResourceForJarClass(final String jarClass, final Collection<Resource> fileResources)
    {
        // Find the real class name by picking the string between the '/' and the '$' or '.'
        int slash = jarClass.lastIndexOf("/");
        int tempDollar = jarClass.lastIndexOf("$");
        int dollar = -1 == tempDollar ? Integer.MAX_VALUE : tempDollar;
        int dot = jarClass.lastIndexOf(".");
        String className = jarClass.substring(slash + 1, Math.min(dollar, dot));

        // Look for the corresponding .amp resources
        return fileResources
            .stream()
            .filter(r -> r.getId().contains(className))
            .findFirst().orElse(null);
    }

    @Override
    public boolean canProcess(final InventoryReport warInventory, final String alfrescoVersion)
    {
        return true;
    }
}
