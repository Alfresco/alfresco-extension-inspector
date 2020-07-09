/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.service;

import static java.util.Collections.emptySet;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.util.DependencyVisitor;
import org.alfresco.ampalyser.model.BeanResource;
import org.alfresco.ampalyser.model.ClasspathElementResource;
import org.alfresco.ampalyser.model.FileResource;
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

    private static final String ORG_ALFRESCO_PREFIX = "org.alfresco";

    @Autowired
    private ConfigService configService;

    private Map<String, Set<BeanResource>> beanOverridesById;
    private Map<String, Set<ClasspathElementResource>> classpathElementsById;
    private Map<String, FileResource> filesByDestination;
    private Map<String, Set<String>> dependenciesPerClass;
    private Set<String> allDependencies;
    private Set<BeanResource> beansOfAlfrescoTypes;

    public Map<String, Set<BeanResource>> retrieveBeanOverridesById()
    {
        if (beanOverridesById == null)
        {
            final Set<String> whitelist = configService.getBeanOverrideWhitelist();

            beanOverridesById = configService
                .getExtensionResources(BEAN)
                .stream()
                .filter(r -> !whitelist.contains(r.getId()))
                .map(r -> (BeanResource) r)
                .collect(groupingBy(Resource::getId, toUnmodifiableSet()));
        }
        return beanOverridesById;
    }

    public Map<String, Set<ClasspathElementResource>> retrieveClasspathElementsById()
    {
        if (classpathElementsById == null)
        {
            classpathElementsById = configService
                .getExtensionResources(CLASSPATH_ELEMENT)
                .stream()
                .map(r -> (ClasspathElementResource) r)
                .collect(groupingBy(
                    Resource::getId,
                    toUnmodifiableSet()
                ));
        }
        return classpathElementsById;
    }

    public Map<String, FileResource> retrieveFilesByDestination()
    {
        final Map<String, String> fileMappings = configService.getFileMappings();

        if (filesByDestination == null)
        {
            filesByDestination = configService
                .getExtensionResources(FILE)
                .stream()
                .map(r -> (FileResource) r)
                .collect(toUnmodifiableMap(r -> computeDestination(r, fileMappings), identity()));
        }
        return filesByDestination;
    }

    public Map<String, Set<String>> retrieveDependenciesPerClass()
    {
        if (dependenciesPerClass == null)
        {
            // each class can have multiple definitions (in different jars)
            final Map<String, List<byte[]>> bytecodePerClass = readBytecodeFromArtifact(
                configService.getExtensionPath());

            dependenciesPerClass = bytecodePerClass
                .entrySet()
                .stream()
                .collect(toUnmodifiableMap(
                    Entry::getKey,
                    e -> e.getValue()
                          .stream()
                          .map(ExtensionResourceInfoService::compileClassDependenciesFromBytecode)
                          .flatMap(Collection::stream)
                          .collect(toUnmodifiableSet())
                ));
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

    public Set<BeanResource> retrieveBeansOfAlfrescoTypes()
    {
        if (beansOfAlfrescoTypes == null)
        {
            beansOfAlfrescoTypes = configService
                .getExtensionResources(BEAN)
                .stream()
                .map(r -> (BeanResource) r)
                .filter(r -> r.getBeanClass() != null)
                .filter(r -> r.getBeanClass().startsWith(ORG_ALFRESCO_PREFIX))
                .collect(toUnmodifiableSet());
        }
        return beansOfAlfrescoTypes;
    }

    private static String computeDestination(final FileResource resource, final Map<String, String> fileMappings)
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
    public static String findMostSpecificMapping(final Map<String, String> fileMappings, final FileResource ampResource)
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
    static Set<String> compileClassDependenciesFromBytecode(final byte[] classData)
    {
        try
        {
            final DependencyVisitor visitor = new DependencyVisitor();
            final ClassReader reader = new ClassReader(classData);
            reader.accept(visitor, ClassReader.EXPAND_FRAMES);
            visitor.visitEnd();

            return visitor
                .getClasses()
                .stream()
                .filter(s -> !s.startsWith("java/")) // strip JDK dependencies
                .map(s -> "/" + s + ".class") // change it to the Inventory Report format
                .collect(toUnmodifiableSet());
        }
        catch (UnsupportedOperationException ignore)
        {
            return emptySet();
        }
    }
}
