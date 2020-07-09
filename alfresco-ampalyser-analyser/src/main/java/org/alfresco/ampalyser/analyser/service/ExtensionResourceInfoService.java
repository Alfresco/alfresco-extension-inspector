/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.service;

import static java.util.Map.Entry;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.alfresco.ampalyser.analyser.util.BytecodeReader.readBytecodeFromArtifact;
import static org.alfresco.ampalyser.model.Resource.Type.BEAN;
import static org.alfresco.ampalyser.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.alfresco.ampalyser.model.Resource.Type.FILE;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.util.DependencyVisitor;
import org.alfresco.ampalyser.model.Resource;
import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Cezar Leahu
 * @author Lucian Tuca
 */
@Component
public class ExtensionResourceInfoService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionResourceInfoService.class);

    @Autowired
    private ConfigService configService;

    private Map<String, Set<Resource>> beanOverridesById;
    private Map<String, Set<Resource>> classpathElementsById;
    private Map<String, Set<Resource>> classpathElementsByName;
    private Map<String, Resource> filesByDestination;
    private Map<String, Set<String>> dependenciesPerClass;
    private Set<String> allDependencies;

    public Map<String, Set<Resource>> retrieveBeanOverridesById()
    {
        if (beanOverridesById == null)
        {
            final Set<String> whitelist = configService.getBeanOverrideWhitelist();

            beanOverridesById = configService
                .getExtensionResources(BEAN)
                .stream()
                .filter(r -> !whitelist.contains(r.getId()))
                .collect(groupingBy(Resource::getId, toUnmodifiableSet()));
        }
        return beanOverridesById;
    }

    public Map<String, Set<Resource>> retrieveClasspathElementsById()
    {
        if (classpathElementsById == null)
        {
            classpathElementsById = configService
                .getExtensionResources(CLASSPATH_ELEMENT)
                .stream()
                .collect(groupingBy(Resource::getId, toUnmodifiableSet()));
        }
        return classpathElementsById;
    }

    public Map<String, Set<Resource>> retrieveClassResourcesByName()
    {
        if (classpathElementsByName == null)
        {
            classpathElementsByName = configService
                .getExtensionResources(CLASSPATH_ELEMENT)
                .stream()
                .filter(r -> r.getId().endsWith(".class"))
                .collect(groupingBy(
                    r -> r.getId().substring(1), //.replaceAll("/", "."), todo?
                    toUnmodifiableSet()
                ));
        }
        return classpathElementsByName;
    }

    public Map<String, Resource> retrieveFilesByDestination()
    {
        final Map<String, String> fileMappings = configService.getFileMappings();

        if (filesByDestination == null)
        {
            filesByDestination = configService
                .getExtensionResources(FILE)
                .stream()
                .collect(toUnmodifiableMap(r -> computeDestination(r, fileMappings), identity()));
        }
        return filesByDestination;
    }

    public Map<String, Set<String>> retrieveDependenciesPerClass()
    {
        if (dependenciesPerClass == null)
        {
            final Map<String, byte[]> bytecodePerClass = readBytecodeFromArtifact(
                configService.getExtensionPath(), configService.getExtensionResources(FILE));

            dependenciesPerClass = bytecodePerClass
                .entrySet()
                .stream()
                .collect(toUnmodifiableMap(Entry::getKey, e -> compileClassDependenciesFromBytecode(e.getValue())));
        }
        return dependenciesPerClass;
    }

    public Set<String> retrieveAllDependencies()
    {
        if (allDependencies == null)
        {
            allDependencies = retrieveDependenciesPerClass()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(toUnmodifiableSet());
        }
        return allDependencies;
    }

    private static String computeDestination(final Resource resource, final Map<String, String> fileMappings)
    {
        // Find the most specific/deepest mapping that we can use
        final String matchingSourceMapping = findMostSpecificMapping(fileMappings, resource);

        // We now know the mapping that should apply and we can calculate the destination
        final String destination =
            matchingSourceMapping.isEmpty() ?
            resource.getId() :
            resource.getId().replaceFirst(matchingSourceMapping,
                fileMappings.get(matchingSourceMapping));

        // If the mapping points to 'root' we might have 2 double '/'
        return destination.startsWith("//") ? destination.substring(1) : destination;
    }

    /**
     * Finds the the most specific (deepest in the file tree) mapping that can apply for the give .amp resource
     *
     * @param fileMappings all the mappings
     * @param ampResource  the .amp resource
     * @return the most specific mapping.
     */
    public static String findMostSpecificMapping(final Map<String, String> fileMappings, final Resource ampResource)
    {
        String matchingSourceMapping = "";
        for (String sourceMapping : fileMappings.keySet())
        {
            if (ampResource.getId().startsWith(sourceMapping + "/") &&
                sourceMapping.length() > matchingSourceMapping.length())
            {
                matchingSourceMapping = sourceMapping;
            }
        }
        return matchingSourceMapping;
    }



    /**
     * For a given .class file provided as byte[] this method finds all the classes this class uses.
     *
     * @param classData the .class file as byte[]
     * @return a {@link Set} of the used classes
     */
    public static Set<String> compileClassDependenciesFromBytecode(final byte[] classData)
    {
        final DependencyVisitor visitor = new DependencyVisitor();
        final ClassReader reader = new ClassReader(classData);
        reader.accept(visitor, ClassReader.EXPAND_FRAMES);
        visitor.visitEnd();

        return visitor
            .getClasses()
            .stream()
            //.map(c -> c.replaceAll("/", ".")) todo?
            .collect(toUnmodifiableSet());
    }
}
