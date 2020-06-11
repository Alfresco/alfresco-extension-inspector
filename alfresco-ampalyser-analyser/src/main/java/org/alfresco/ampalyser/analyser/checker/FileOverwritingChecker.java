/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static org.alfresco.ampalyser.model.Resource.Type.FILE;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.alfresco.ampalyser.analyser.result.FileOverwriteResult;
import org.alfresco.ampalyser.analyser.result.Result;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Checker for detecting File Overwrites conflicts.
 *
 * @author Lucian Tuca
 */
@Component
public class FileOverwritingChecker implements Checker
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileOverwritingChecker.class);

    public static final String FILE_MAPPING_NAME = "file-mapping.properties";
    private static final String INCLUDE_DEFAULT_PROPERTY_KEY = "include.default";

    private static final Map<String, String> DEFAULT_MAPPINGS = Map.of(
        "/config","/WEB-INF/classes",
        "/lib", "/WEB-INF/lib",
        "/licenses", "/WEB-INF/licenses",
        "/web/jsp", "/jsp",
        "/web/css", "/css",
        "/web/images", "/images",
        "/web/scripts", "/scripts",
        "/web/php", "/php"
    );

    @Override
    public List<Result> processInternal(InventoryReport warReport, InventoryReport ampReport, Map<String, Object> extraInfo)
    {
        List<Result> results = new LinkedList<>();
        List<Properties> foundMappingProperties = (List<Properties>) extraInfo.get(FILE_MAPPING_NAME);
        Map<String, String> completeMappingProperties = computeMappings(foundMappingProperties);

        // Check every resource for conflicts
        for (Resource ampResource : ampReport.getResources().get(FILE))
        {
            // Find the most specific/deepest mapping that we can use
            String matchingSourceMapping = findMostSpecificMapping(completeMappingProperties, ampResource);

            // We now know the mapping that should apply and we can calculate the destination
            String destination = matchingSourceMapping.isEmpty() ? ampResource.getId() :
                    ampResource.getId().replaceFirst(matchingSourceMapping, completeMappingProperties.get(matchingSourceMapping));

            // If the mapping points to 'root' we might have 2 double '/'
            destination = destination.startsWith("//") ? destination.substring(1) : destination;

            // Iterate through the war FILE resources and check if the calculated destination matches any of the existing resources
            for (Resource warResource : warReport.getResources().get(FILE))
            {
                if (warResource.getId().equals(destination))
                {
                    FileOverwriteResult newResult = new FileOverwriteResult(
                        ampResource,
                        warResource,
                        matchingSourceMapping.isEmpty() ? null : Map.of(matchingSourceMapping, completeMappingProperties.get(matchingSourceMapping)),
                        warReport.getAlfrescoVersion()
                    );
                    results.add(newResult);
                }
            }
        }

        return results;
    }

    /**
     * Finds the the most specific (deepest in the file tree) mapping that can apply for the give .amp resource
     * @param completeMappingProperties all the mappings
     * @param ampResource the .amp resource
     * @return the most specific mapping.
     */
    private static String findMostSpecificMapping(Map<String, String> completeMappingProperties, Resource ampResource)
    {
        String matchingSourceMapping = "";
        for (String sourceMapping : completeMappingProperties.keySet())
        {
            if (ampResource.getId().startsWith(sourceMapping + "/") && sourceMapping.length() > matchingSourceMapping.length())
            {
                matchingSourceMapping = sourceMapping;
            }
        }
        return matchingSourceMapping;
    }

    @Override
    public boolean canProcessEntry(InventoryReport warReport, InventoryReport ampReport, Map<String, Object> extraInfo)
    {
        return true;
    }

    /**
     * Computes all the mappings based on the found files and the boolean flags.
     *
     * @param foundMappingProperties
     * @return A map where the key is the amp (source) location and the value the war (target) location.
     */
    private static Map<String, String> computeMappings(List<Properties> foundMappingProperties)
    {
        Map<String, String> mappings = new HashMap<>();
        // Flatten the Properties objects
        for (Properties properties : foundMappingProperties)
        {

            for (Map.Entry entry : properties.entrySet())
            {
                // Add the default mappings if the 'include.default' is set to true
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                if (INCLUDE_DEFAULT_PROPERTY_KEY.equals(key) && Boolean.parseBoolean(value))
                {
                    mappings.putAll(DEFAULT_MAPPINGS);
                }

                // and filter to only keep entries that refer to path mappings
                if (key.startsWith("/") && value.startsWith("/"))
                {
                    mappings.put(key, value);
                }
            }
        }
        return mappings;
    }
}
