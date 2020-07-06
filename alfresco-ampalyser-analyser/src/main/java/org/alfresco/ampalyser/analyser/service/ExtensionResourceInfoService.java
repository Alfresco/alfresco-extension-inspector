package org.alfresco.ampalyser.analyser.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.alfresco.ampalyser.model.Resource.Type.BEAN;
import static org.alfresco.ampalyser.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.alfresco.ampalyser.model.Resource.Type.FILE;

import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExtensionResourceInfoService
{
    @Autowired
    private ConfigService configService;

    private Map<String, Set<Resource>> beanOverridesById;
    private Map<String, Set<Resource>> classpathElementsById;
    private Map<String, Resource> filesByDestination;

    public Map<String, Set<Resource>> retrieveBeanOverridesById()
    {
        if (beanOverridesById == null)
        {
            final Set<String> whitelist = configService.getBeanOverrideWhitelist();

            beanOverridesById = configService
                .getExtensionResources(BEAN)
                .stream()
                .filter(r -> !whitelist.contains(r.getId()))
                .collect(groupingBy(Resource::getId, toSet()));
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
                .collect(groupingBy(Resource::getId, toSet()));
        }
        return classpathElementsById;
    }

    public Map<String, Resource> retrieveFilesByDestination()
    {
        final Map<String, String> fileMappings = configService.getFileMappings();

        if(filesByDestination ==null) {
            filesByDestination = configService
                .getExtensionResources(FILE)
                .stream()
                .collect(toMap(r -> computeDestination(r, fileMappings), identity()));
        }
        return filesByDestination;
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
}
