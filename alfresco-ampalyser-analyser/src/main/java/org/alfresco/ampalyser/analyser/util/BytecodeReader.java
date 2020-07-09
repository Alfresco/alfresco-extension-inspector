package org.alfresco.ampalyser.analyser.util;

import static java.util.Collections.singletonList;
import static java.util.Map.Entry;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static org.alfresco.ampalyser.commons.InventoryUtils.extract;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
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
    public static Map<String, List<byte[]>> readBytecodeFromArtifact(final String artifactPath)
    {
        return artifactPath.endsWith(".jar") ?
               readBytecodeFromJarArtifact(artifactPath) :
               readBytecodeFromAmpArtifact(artifactPath);
    }

    /**
     * Extract the bytecode from all the classes in the given JAR.
     *
     * @param jarPath
     * @return A map of class_name -> bytecode.
     */
    private static Map<String, List<byte[]>> readBytecodeFromJarArtifact(final String jarPath)
    {
        try (final InputStream inputStream = new BufferedInputStream(new FileInputStream(jarPath)))
        {
            return extractClassBytecodeFromJar(inputStream)
                .entrySet()
                .stream()
                .collect(toUnmodifiableMap(
                    Entry::getKey,
                    e -> singletonList(e.getValue())
                ));
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to open and iterate through the provided JAR file: " + jarPath, e);
            throw new RuntimeException("Failed to open and iterate through the provided JAR file: " + jarPath, e);
        }
    }

    /**
     * Extract the bytecode from all the classes from all the JARs in the given AMP.
     *
     * @param ampPath
     * @return A map of class_name -> bytecode.
     */
    private static Map<String, List<byte[]>> readBytecodeFromAmpArtifact(final String ampPath)
    {
        try (final ZipFile zipFile = new ZipFile(ampPath))
        {
            return zipFile
                .stream()
                .filter(entry -> entry.getName().endsWith(".jar"))
                .map(entry -> {
                    try (final InputStream inputStream = zipFile.getInputStream(entry))
                    {
                        return extractClassBytecodeFromJar(new BufferedInputStream(inputStream));
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException("Failed to read AMP Zip entry (*.jar)", e);
                    }
                })
                .flatMap(m -> m.entrySet().stream())
                .collect(groupingBy( // we can have the same class in multiple JARs
                    Entry::getKey,
                    mapping(Entry::getValue, toList())
                ));
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to open and iterate through the provided AMP file: " + ampPath, e);
            throw new RuntimeException("Failed to open and iterate through the provided AMP file: " + ampPath, e);
        }
    }

    static Map<String, byte[]> extractClassBytecodeFromJar(final InputStream inputStream) throws IOException
    {
        final Map<String, byte[]> javaClasses = new HashMap<>();

        final ZipArchiveInputStream jarZis = new ZipArchiveInputStream(inputStream);

        // Iterate through the .jar
        ArchiveEntry entry;
        while ((entry = jarZis.getNextEntry()) != null)
        {
            final String jzeName = entry.getName();
            if (jzeName.endsWith(".class"))
            {
                javaClasses.put("/" + jzeName, extract(jarZis));
                LOGGER.debug("Found a class " + jzeName);
            }
        }

        return javaClasses;
    }
}
