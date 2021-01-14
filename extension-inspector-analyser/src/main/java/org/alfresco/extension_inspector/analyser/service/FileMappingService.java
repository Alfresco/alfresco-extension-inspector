/*
 * Copyright 2021 Alfresco Software, Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.alfresco.extension_inspector.analyser.service;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Map.Entry;
import static java.util.stream.Collectors.toUnmodifiableSet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.alfresco.extension_inspector.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Lucian Tuca
 */
@Component
public class FileMappingService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileMappingService.class);

    private static final String FILE_MAPPING_NAME = "file-mapping.properties";
    private static final String INCLUDE_DEFAULT_PROPERTY_KEY = "include.default";

    private static final Map<String, String> DEFAULT_MAPPINGS = unmodifiableMap(Map.of(
        "/config", "/WEB-INF/classes",
        "/lib", "/WEB-INF/lib",
        "/licenses", "/WEB-INF/licenses",
        "/web/jsp", "/jsp",
        "/web/css", "/css",
        "/web/images", "/images",
        "/web/scripts", "/scripts",
        "/web/php", "/php"
    ));

    /**
     * Extracts the custom file mapping information from the amp artifact and combines that with the default AMP
     * mappings.
     *
     * @param ampPath      The filepath of the .amp required to open the .zip file and read the file-mapping.properties file(s)
     * @param ampFileResources The FILE resources of the amp specified by the previous argument
     * @return The complete file mapping for a given amp (as it is understood by the MMT tool).
     */
    public Map<String, String> compileFileMappings(final String ampPath, Collection<Resource> ampFileResources)
    {
        final List<Properties> fileMappingFiles = findFileMappingFiles(ampPath, ampFileResources);
        return computeMappings(fileMappingFiles);
    }

    /**
     * Extracts the mapping options specified in the .amp as {@link List}s of {@link Properties}
     *
     * @param ampPath      The filepath of the .amp required to open the .zip file and read the file-mapping.properties file(s)
     * @param ampFileResources The .amp resources
     * @return
     */
    private static List<Properties> findFileMappingFiles(String ampPath, Collection<Resource> ampFileResources)
    {
        // Look for "file-mapping.properties"
        final Set<Resource> mappingResources = ampFileResources
            .stream()
            .filter(fileResource -> (fileResource.getId() != null && fileResource.getId().contains(FILE_MAPPING_NAME)))
            .collect(toUnmodifiableSet());

        // If no resource is found quickly return before unzipping and iterating through the whole amp (.zip) file
        if (mappingResources.isEmpty())
        {
            LOGGER.info(FILE_MAPPING_NAME + " was not found in the provided .amp. Continuing with default mapping.");
            return emptyList();
        }

        try
        {
            final List<Properties> foundProperties = new ArrayList<>();

            final ZipFile zipFile = new ZipFile(ampPath);
            for (Resource resource : mappingResources)
            {
                // Trim the first slash when looking in the .zip
                final ZipEntry entry = zipFile.getEntry(resource.getId().substring(1));
                if (FILE_MAPPING_NAME.equals(entry.getName()))
                {
                    final InputStream is = zipFile.getInputStream(entry);
                    final Properties properties = new Properties();
                    properties.load(is);
                    foundProperties.add(properties);
                }
            }

            return foundProperties;
        }
        catch (IOException e)
        {
            LOGGER.warn("Failed to read from the " + FILE_MAPPING_NAME + " although it was found.");
            return emptyList();
        }
    }

    /**
     * Computes all the mappings based on the found files and the boolean flags.
     *
     * @param foundMappingProperties
     * @return A map where the key is the amp (source) location and the value the war (target) location.
     */
    private static Map<String, String> computeMappings(List<Properties> foundMappingProperties)
    {
        boolean includeDefaultMappings = true;
        final Map<String, String> mappings = new HashMap<>();

        // Flatten the Properties objects
        for (Properties properties : foundMappingProperties)
        {
            for (Entry entry : properties.entrySet())
            {
                // Add the default mappings if the 'include.default' is set to true
                final String key = entry.getKey().toString();
                final String value = entry.getValue().toString();
                if (INCLUDE_DEFAULT_PROPERTY_KEY.equals(key) && !Boolean.parseBoolean(value))
                {
                    LOGGER.info(
                        "The " + FILE_MAPPING_NAME + " was explicitly configured to ignore the default mappings.");
                    includeDefaultMappings = false;
                }

                // and filter to only keep entries that refer to path mappings
                if (key.startsWith("/") && value.startsWith("/"))
                {
                    mappings.put(key, value);
                }
            }
        }

        if (includeDefaultMappings)
        {
            mappings.putAll(DEFAULT_MAPPINGS);
        }

        return unmodifiableMap(mappings);
    }
}
