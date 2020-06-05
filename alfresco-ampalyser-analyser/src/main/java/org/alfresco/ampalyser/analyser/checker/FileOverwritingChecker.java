/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.Collections.emptyList;
import static org.alfresco.ampalyser.model.Resource.Type.FILE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

    private Map<String, String> defaultMappingProperties = Map.of(
        "/config","/WEB-INF/classes",
        "/lib", "/WEB-INF/lib",
        "/licenses", "/WEB-INF/licenses",
        "/web/jsp", "/jsp",
        "/web/css", "/css",
        "/web/images", "/images",
        "/web/scripts", "/scripts",
        "/web/php", "/php"
    );

    private Map<String, String> completeMappingProperties = new HashMap<>();

    @Override
    public List<Result> processInternal(InventoryReport warReport, InventoryReport ampReport, Map<String, Object> extraInfo)
    {
        List<Properties> foundMappingProperties = (List<Properties>) extraInfo.get(FILE_MAPPING_NAME);

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
                    completeMappingProperties.putAll(defaultMappingProperties);
                }

                // and filter to only keep entries that refer to path mappings
                if (key.startsWith("/") && value.startsWith("/"))
                {
                    completeMappingProperties.put(key, value);
                }
            }
        }

        //
        for (Resource resource : ampReport.getResources().get(FILE))
        {

        }

        return emptyList();
    }

    @Override
    public Result.Type getType()
    {
        return Result.Type.FILE_OVERWRITE;
    }

    @Override
    public boolean canProcessEntry(InventoryReport warReport, InventoryReport ampReport, Map<String, Object> extraInfo)
    {
        return true;
    }
}
