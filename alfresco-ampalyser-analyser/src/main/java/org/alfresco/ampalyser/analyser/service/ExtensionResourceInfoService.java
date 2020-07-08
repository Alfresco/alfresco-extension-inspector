package org.alfresco.ampalyser.analyser.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.alfresco.ampalyser.commons.InventoryUtils.extract;
import static org.alfresco.ampalyser.model.Resource.Type.BEAN;
import static org.alfresco.ampalyser.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.alfresco.ampalyser.model.Resource.Type.FILE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.alfresco.ampalyser.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExtensionResourceInfoService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionResourceInfoService.class);

    @Autowired
    private ConfigService configService;

    private Map<String, Set<Resource>> beanOverridesById;
    private Map<String, Set<Resource>> classpathElementsById;
    private Map<String, Resource> filesByDestination;
    private Map<String, byte[]> bytecodePerClass;

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

        if (filesByDestination == null)
        {
            filesByDestination = configService
                .getExtensionResources(FILE)
                .stream()
                .collect(toMap(r -> computeDestination(r, fileMappings), identity()));
        }
        return filesByDestination;
    }

    // TODO check if any further processing can be done here (instead of returning bytecode, maybe we can compute the actual necessary info
    public Map<String, byte[]> retrieveBytecodePerClass()
    {
        if (bytecodePerClass == null)
        {
            bytecodePerClass = findClasses(configService.getExtensionPath(), configService.getExtensionResources(FILE));
        }
        return bytecodePerClass;
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
     * Finds and computes a {@link Map} containing all the .class files with the filename as the key and byte[] data as the value
     *
     * @param ampPath the location of the amp
     * @return a {@link Map} containing all the .class files with the filename as the key and byte[] data as the value
     */
    private static Map<String, byte[]> findClasses(final String ampPath, final Collection<Resource> fileResources)
    {
        final Map<String, byte[]> javaClasses = new HashMap<>();

        int lastSlash = ampPath.lastIndexOf("/");
        int lastDot = ampPath.lastIndexOf(".");
        String ampNameWithVersion = ampPath.substring(lastSlash + 1, lastDot);
        LOGGER.info("Looking for " + ampNameWithVersion + " jar");

        try
        {
            // Iterate through the .amp
            Resource ampJarResource = fileResources
                .stream()
                .filter(r -> r.getId().contains(ampNameWithVersion))
                .findFirst().orElse(null);

            ZipFile zipFile = new ZipFile(ampPath);
            ZipEntry ampJarEntry = zipFile.getEntry(ampJarResource.getId().substring(1));
            InputStream inputStream = zipFile.getInputStream(ampJarEntry);

            // Extract the jar
            byte[] data = extract(inputStream);
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ZipInputStream jarZis = new ZipInputStream(bis);
            ZipEntry jarZe = jarZis.getNextEntry();

            // Iterate through the .jar
            while (jarZe != null)
            {
                String jzeName = jarZe.getName();
                if (jzeName.endsWith(".class"))
                {
                    javaClasses.put(
                        jzeName.substring(0, jzeName.length() - 6).replaceAll("/", "."),
                        extract(jarZis));
                    LOGGER.debug("Found a class " + jzeName);
                }
                jarZis.closeEntry();
                jarZe = jarZis.getNextEntry();
            }
        }

        catch (IOException ioe)
        {
            LOGGER.error("Failed to open and iterate through the provided file: " + ampPath, ioe);
            throw new RuntimeException("Failed to open and iterate through the provided file: " + ampPath, ioe);
        }

        return javaClasses;
    }
}
