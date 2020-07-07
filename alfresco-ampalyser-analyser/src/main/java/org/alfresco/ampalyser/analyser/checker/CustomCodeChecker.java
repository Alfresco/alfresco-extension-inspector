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
        Map<String, Set<String>> ampClassesDependencies = new HashMap<>();

        // Visit each class to search for all its the dependencies
        for (Map.Entry<String, byte[]> entry : ampClasses.entrySet())
        {
            final DependencyVisitor visitor = new DependencyVisitor();
            final ClassReader reader = new ClassReader(entry.getValue());
            reader.accept(visitor, ClassReader.EXPAND_FRAMES);
            visitor.visitEnd();

            ampClassesDependencies.put(entry.getKey(), visitor.getClasses());
        }

        // Create a Map of the AlfrescoPublicApi with the class/id as the key and whether or not it is deprecated as the value
        Map<String, Boolean> alfrescoPublicApis = warInventory.getResources().get(ALFRESCO_PUBLIC_API).stream()
            .map(r -> (AlfrescoPublicApiResource) r)
            .collect(toMap(Resource::getId, AlfrescoPublicApiResource::isDeprecated));

        // For each amp class
        // Only keep as 'invalid' dependencies
        // the 'org.alfresco' classes
        // that do not belong to the .amp classes
        // and that are not annotated with the @AlfrescoPublicApi OR are annotated but the class is deprecated
        Map<String, Set<String>> filteredAmpClassesDependencies = ampClassesDependencies.entrySet().stream()
            .collect(toMap(
                Map.Entry::getKey,
                e -> e
                    .getValue()
                    .stream()
                    .filter(s ->
                            s.startsWith("org/alfresco") &&
                            !ampClasses.containsKey(s + ".class") &&
                            (!alfrescoPublicApis.containsKey(s.replaceAll("/", ".")) ||
                            (alfrescoPublicApis.containsKey(s.replaceAll("/", ".")) && alfrescoPublicApis.get(s.replaceAll("/", ".")))))
                    .collect(toSet())
            ));

        return filteredAmpClassesDependencies.entrySet().stream()
            .filter(e -> !e.getValue().isEmpty())
            .map(e -> new CustomCodeConflict(findAmpResourceForJarClass(e.getKey(), ampInventory), null, e.getValue(), (String) extraInfo.get(ALFRESCO_VERSION)))
            .collect(toList());
    }

    /**
     * Given a java class name (found in the .jar lib of the .amp) this method find the corresponding .amp {@link org.alfresco.ampalyser.model.FileResource}
     * @param jarClass the Java class name that was found in the .jar
     * @param inventoryReport the .amp inventory to look in
     * @return the {@link org.alfresco.ampalyser.model.FileResource} coressponding to the provided class name
     */
    private Resource findAmpResourceForJarClass(String jarClass, InventoryReport inventoryReport)
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
