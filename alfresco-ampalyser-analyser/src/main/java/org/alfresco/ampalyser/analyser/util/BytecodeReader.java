package org.alfresco.ampalyser.analyser.util;

import static java.util.Collections.emptyMap;
import static org.alfresco.ampalyser.commons.InventoryUtils.extract;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.alfresco.ampalyser.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Cezar Leahu
 * @author Lucian Tuca
 */
public class BytecodeReader
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BytecodeReader.class);

    /**
     * Finds and computes a {@link Map} containing all the .class files with the filename as the key and byte[] data as the value
     *
     * @param artifactPath the location of the amp
     * @return a {@link Map} containing all the .class files with the filename as the key and byte[] data as the value
     */
    public static Map<String, byte[]> readBytecodeFromArtifact(
        final String artifactPath,
        final Collection<Resource> fileResources)
    {
        return artifactPath.endsWith(".jar") ?
               readBytecodeFromJarArtifact(artifactPath) :
               readBytecodeFromAmpArtifact(artifactPath, fileResources);
    }

    private static Map<String, byte[]> readBytecodeFromJarArtifact(final String artifactPath)
    {
        try (final ZipInputStream inputStream = new ZipInputStream(
            new BufferedInputStream(new FileInputStream(artifactPath))))
        {
            return extractClassBytecodeFromJar(inputStream);
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to open and iterate through the provided JAR file: " + artifactPath, e);
            throw new RuntimeException("Failed to open and iterate through the provided JAR file: " + artifactPath, e);
        }
    }

    private static Map<String, byte[]> readBytecodeFromAmpArtifact(
        final String artifactPath,
        final Collection<Resource> fileResources)
    {
        final String ampNameWithVersion = artifactPath.substring(
            artifactPath.lastIndexOf("/") + 1,
            artifactPath.lastIndexOf(".")
        );
        LOGGER.info("Looking for " + ampNameWithVersion + " jar");

        // Iterate through the .amp
        final Resource ampJarResource = fileResources
            .stream()
            .filter(r -> r.getId().contains(ampNameWithVersion))
            .findFirst()
            .orElse(null);

        if (ampJarResource == null)
        {
            LOGGER.info("The extension JAR artifact was not found in the AMP");
            return emptyMap();
        }

        try (final ZipFile zipFile = new ZipFile(artifactPath))
        {
            final ZipEntry ampJarEntry = zipFile.getEntry(ampJarResource.getId().substring(1));
            final InputStream inputStream = zipFile.getInputStream(ampJarEntry);

            // TODO These three lines are probably not necessary (the intermediate stream)
            //final byte[] jarData = extract(inputStream);
            //final ByteArrayInputStream bis = new ByteArrayInputStream(jarData);
            //return extractClassBytecodeFromJar(bis);
            return extractClassBytecodeFromJar(new BufferedInputStream(inputStream));
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to open and iterate through the provided AMP file: " + artifactPath, e);
            throw new RuntimeException("Failed to open and iterate through the provided AMP file: " + artifactPath, e);
        }
    }

    private static Map<String, byte[]> extractClassBytecodeFromJar(final InputStream inputStream) throws IOException
    {
        final Map<String, byte[]> javaClasses = new HashMap<>();

        final ZipInputStream jarZis = new ZipInputStream(inputStream);
        ZipEntry jarZe = jarZis.getNextEntry();

        // Iterate through the .jar
        while (jarZe != null)
        {
            final String jzeName = jarZe.getName();
            if (jzeName.endsWith(".class"))
            {
                javaClasses.put(
                    jzeName,
                    //jzeName.substring(0, jzeName.length() - 6), //.replaceAll("/", "."), todo?
                    extract(jarZis));
                LOGGER.debug("Found a class " + jzeName);
            }
            jarZis.closeEntry();
            jarZe = jarZis.getNextEntry();
        }

        return javaClasses;
    }
}
