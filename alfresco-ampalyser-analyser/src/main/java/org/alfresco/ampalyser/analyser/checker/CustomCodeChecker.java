/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.alfresco.ampalyser.model.Resource.Type.ALFRESCO_PUBLIC_API;
import static org.alfresco.ampalyser.model.Resource.Type.FILE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.CustomCodeConflict;
import org.alfresco.ampalyser.analyser.service.DependencyVisitor;
import org.alfresco.ampalyser.model.AlfrescoPublicApiResource;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public static final String AMP_JAVA_CLASSES = "AMP_JAVA_CLASSES";

    @Override
    public List<Conflict> processInternal(InventoryReport ampInventory,
        InventoryReport warInventory, Map<String, Object> extraInfo)
    {
        Map<String, byte[]> ampClasses = (Map<String, byte[]>) extraInfo.get(AMP_JAVA_CLASSES);
        Map<String, Set<String>> ampClassesDependencies = new HashMap<>(ampClasses.size());

        // Visit each class to search for all its the dependencies
        ampClasses.forEach((k,v) -> ampClassesDependencies.put(k, findDependenciesForClass(v)));

        // Create a Map of the AlfrescoPublicApi with the class/id as the key and whether or not it is deprecated as the value
        Map<String, Boolean> alfrescoPublicApis = warInventory.getResources().get(ALFRESCO_PUBLIC_API).stream()
            .map(r -> (AlfrescoPublicApiResource) r)
            .collect(toMap(Resource::getId, AlfrescoPublicApiResource::isDeprecated));

        // For each amp class only keep its invalid dependencies
        Map<String, Set<String>> filteredAmpClassesDependencies = ampClassesDependencies.entrySet().stream()
            .collect(toMap(
                Map.Entry::getKey,
                e -> e
                    .getValue()
                    .stream()
                    .filter(c -> isInvalidAlfrescoDependency(c, alfrescoPublicApis, ampClasses))
                    .collect(toSet())
            ));

        return filteredAmpClassesDependencies.entrySet().stream()
            .filter(e -> !e.getValue().isEmpty())
            .map(e -> new CustomCodeConflict(findAmpResourceForJarClass(e.getKey(), ampInventory), null, e.getValue(), (String) extraInfo.get(ALFRESCO_VERSION)))
            .collect(toList());
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
     * @param alfrescoPublicApis
     * @param ampClasses
     * @return whether or not this class is an invalid Alfresco dependency
     */
    private static boolean isInvalidAlfrescoDependency(String clazz, Map<String, Boolean> alfrescoPublicApis, Map<String, byte[]> ampClasses)
    {
        return clazz.startsWith("org/alfresco") &&
            !ampClasses.containsKey(clazz + ".class") &&
            (!alfrescoPublicApis.containsKey(clazz.replaceAll("/", ".")) ||
             (alfrescoPublicApis.containsKey(clazz.replaceAll("/", ".")) && alfrescoPublicApis.get(clazz.replaceAll("/", "."))));
    }

    /**
     * For a given .class file provided as byte[] this method finds all the classes this class uses.
     *
     * @param classData the .class file as byte[]
     * @return a {@link Set} of the used classes
     */
    private static Set<String> findDependenciesForClass(byte[] classData)
    {
        final DependencyVisitor visitor = new DependencyVisitor();
        final ClassReader reader = new ClassReader(classData);
        reader.accept(visitor, ClassReader.EXPAND_FRAMES);
        visitor.visitEnd();

        return visitor.getClasses();
    }

    /**
     * Given a java class name (found in the .jar lib of the .amp) this method find the corresponding .amp {@link org.alfresco.ampalyser.model.FileResource}
     * @param jarClass the Java class name that was found in the .jar
     * @param inventoryReport the .amp inventory to look in
     * @return the {@link org.alfresco.ampalyser.model.FileResource} coressponding to the provided class name
     */
    private static Resource findAmpResourceForJarClass(String jarClass, InventoryReport inventoryReport)
    {
        // Find the real class name by picking the string between the '/' and the '$' or '.'
        int slash = jarClass.lastIndexOf("/");
        int tempDollar = jarClass.lastIndexOf("$");
        int dollar = -1 == tempDollar ? Integer.MAX_VALUE : tempDollar;
        int dot = jarClass.lastIndexOf(".");
        String className = jarClass.substring(slash + 1, Math.min(dollar, dot));

        // Look for the corresponding .amp resources
        return inventoryReport.getResources().get(FILE).stream()
            .filter(r -> r.getId().contains(className))
            .findFirst().orElse(null);
    }

    @Override
    public boolean canProcess(InventoryReport ampInventory, InventoryReport warInventory,
        Map<String, Object> extraInfo)
    {
        return true;
    }
}
