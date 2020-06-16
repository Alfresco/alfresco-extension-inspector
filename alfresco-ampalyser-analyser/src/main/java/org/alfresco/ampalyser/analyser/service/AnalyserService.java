package org.alfresco.ampalyser.analyser.service;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.alfresco.ampalyser.analyser.checker.BeanOverwritingChecker.BEAN_OVERRIDING_WHITELIST;
import static org.alfresco.ampalyser.analyser.checker.Checker.ALFRESCO_VERSION;
import static org.alfresco.ampalyser.analyser.checker.FileOverwritingChecker.FILE_MAPPING_NAME;
import static org.alfresco.ampalyser.model.Resource.Type.FILE;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.alfresco.ampalyser.analyser.comparators.WarComparatorService;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.alfresco.ampalyser.inventory.service.InventoryService;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AnalyserService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyserService.class);

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private WarInventoryReportStore warInventoryStore;

    @Autowired
    private WarComparatorService warComparatorService;

    @Autowired
    private ObjectMapper objectMapper;

    public void analyse(final String ampPath, final SortedSet<String> alfrescoVersions, final String whitelistFilePath)
    {
        // build the *ampInventoryReport*:
        final InventoryReport ampInventory = inventoryService.extractInventoryReport(ampPath);

        final Map<String, List<Conflict>> conflictsPerWarVersion = alfrescoVersions
            .stream()
            .collect(toMap(identity(), v -> warComparatorService.findConflicts(
                ampInventory,
                warInventoryStore.retrieve(v),
                Map.of(
                    ALFRESCO_VERSION, v,
                    FILE_MAPPING_NAME, findFileMappingFiles(ampPath, ampInventory.getResources().get(FILE)),
                    BEAN_OVERRIDING_WHITELIST, loadBeanOverridingWhiteList(whitelistFilePath)
                ))
            ));

        //TODO ACS-192 Process results and generate output, e.g.
        // > /foo/bar.jar - conflicting with 4.2.0, 4.2.1, 4.2.3, 4.2.4, 4.2.5
    }

    /**
     * Extracts the mapping options specified in the .amp as {@link List}s of {@link Properties}
     * @param ampPath The filepath of the .amp required to open the .zip file and read the file-mapping.properties file(s)
     * @param ampResources The .amp resources
     * @return
     */
    private static List<Properties> findFileMappingFiles(String ampPath, Collection<Resource> ampResources)
    {
        List<Properties> foundProperties = new ArrayList<>();

        // Look for "file-mapping.properties"
        List<Resource> mappingResources = ampResources.stream()
            .filter(fileResource -> (fileResource.getId() != null && fileResource.getId().contains(FILE_MAPPING_NAME)))
            .collect(toUnmodifiableList());

        // If no resource is found quickly return before unzipping and iterating through the whole amp (.zip) file
        if (mappingResources.size() < 1)
        {
            LOGGER.info(FILE_MAPPING_NAME + " was not found in the provided .amp. Continuing with default mapping.");
            return emptyList();
        }

        try
        {
            ZipFile zipFile = new ZipFile(ampPath);
            for (Resource resource : mappingResources)
            {
                // Trim the first slash when looking in the .zip
                ZipEntry entry = zipFile.getEntry(resource.getId().substring(1));
                if (FILE_MAPPING_NAME.equals(entry.getName()))
                {
                    InputStream is = zipFile.getInputStream(entry);
                    Properties properties = new Properties();
                    properties.load(is);
                    foundProperties.add(properties);
                }
            }
        }
        catch (IOException e)
        {
            LOGGER.warn("Failed to read from the " + FILE_MAPPING_NAME + " although it was found.");
            return emptyList();
        }

        return foundProperties;
    }

    /**
     * Reads and loads whitelist for the beans in a war that can be overridden when an .amp is applied
     *
     * @return a {@link Set} of the whitelisted beans (that can be overridden).
     */
    private Set<String> loadBeanOverridingWhiteList(String whitelistFilePath)
    {
        if (whitelistFilePath == null)
        {
            return emptySet();
        }

        try
        {
            return objectMapper.readValue(new FileInputStream(whitelistFilePath), new TypeReference<>() {});
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to read Bean Overriding Whitelist file: " + whitelistFilePath, e);
            throw new RuntimeException("Failed to read Bean Overriding Whitelist file: " + whitelistFilePath, e);
        }
    }
}
