package org.alfresco.extension_inspector.analyser.util;

import static java.util.Collections.singletonList;
import static java.util.Map.Entry;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static org.alfresco.extension_inspector.commons.InventoryUtils.extract;

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
import org.springframework.stereotype.Component;

/**
 * The methods in this class can extract the class bytecode from an artifact.
 *
 * @author Cezar Leahu
 * @author Lucian Tuca
 */
@Component
public class BytecodeReader
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BytecodeReader.class);

    /**
     * Finds and computes a {@link Map} containing all the .class files with the filename as the key and byte[] data as the value
     *
     * @param artifactPath the location of the amp
     * @return a {@link Map} containing all the .class files with the filename as the key and byte[] data as the value
     */
    public Map<String, List<byte[]>> readArtifact(final String artifactPath)
    {
        return artifactPath.endsWith(".jar") ?
               readJarArtifact(artifactPath) :
               readAmpArtifact(artifactPath);
    }

    /**
     * Extract the bytecode from all the classes in the given JAR.
     * <p/>
     * In case of JARs, each entry value will be a list with one single element -
     * as a JAR artifact cannot contain multiple instances of the same Class.
     *
     * @param jarPath
     * @return A map of (class_name -> {bytecode}).
     */
    public Map<String, List<byte[]>> readJarArtifact(final String jarPath)
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
     * <p/>
     * In case of AMPs, each entry value is a list of one or more elements, as an AMP
     * can contain multiple JARs and thus multiple instances of the same Class.
     *
     * @param ampPath
     * @return A map of (class_name -> {bytecode}).
     */
    public Map<String, List<byte[]>> readAmpArtifact(final String ampPath)
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
                    mapping(Entry::getValue, toUnmodifiableList())
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
