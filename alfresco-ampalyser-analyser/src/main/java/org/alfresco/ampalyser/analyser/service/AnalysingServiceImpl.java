/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.service;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.alfresco.ampalyser.analyser.checker.FileOverwritingChecker.FILE_MAPPING_NAME;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.alfresco.ampalyser.analyser.Analyser;
import org.alfresco.ampalyser.analyser.parser.InventoryParser;
import org.alfresco.ampalyser.analyser.result.Result;
import org.alfresco.ampalyser.inventory.service.InventoryService;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * The backbone of the application. Triggers the workflow for the analysis process.
 *
 * @author Lucian Tuca
 */
@Service
public class AnalysingServiceImpl implements AnalysingService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysingServiceImpl.class);

    @Autowired
    private InventoryParser inventoryParser;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private Analyser analyser;

    @Override
    public int analyse(String ampPath, String warInventoryReportPath)
    {
        InventoryReport warInventoryReport;
        InventoryReport ampInventoryReport;

        // Load the report for the target war
        warInventoryReport = inventoryParser.parseReport(warInventoryReportPath);
        if (warInventoryReport == null)
        {
            // Exit with a code for parsing fail
            LOGGER.error("Failed to load war inventory report from file: " + warInventoryReportPath);
            return 1;
        }

        // Create the inventory for the source amp
        ampInventoryReport = inventoryService.extractInventoryReport(ampPath);
        if (ampInventoryReport == null)
        {
            // Exit with a code for parsing fail
            LOGGER.error("Failed to extract amp inventory report from file: " + ampPath);
            return 2;
        }

        // Create a map of extraInfo that might be required by each checker.
        // e.g. the FileOverwritingChecker needs the content of the 'file-mapping.properties' file

        Map<String, Object> extraInfo = new HashMap<>();
        extraInfo.put(FILE_MAPPING_NAME, findFileMappingFiles(ampPath, ampInventoryReport));

        try
        {
            List<Result> results = analyser
                .startAnalysis(warInventoryReport, ampInventoryReport, extraInfo);

            // TODO: remove once we have a reporting mechanism (e.g. write to json file)
            ObjectMapper om = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            System.out.println(om.writeValueAsString(results));
            //
        }
        catch (IOException ioe)
        {
            LOGGER.error("Analysis could not be performed.", ioe);
        }

        return 0;
    }

    private static List<Properties> findFileMappingFiles(String ampPath, InventoryReport ampInventoryReport)
    {
        List<Properties> foundProperties = new ArrayList<>();

        // Look for "file-mapping.properties"
        List<Resource> mappingResources = ampInventoryReport.getResources().get(Resource.Type.FILE).stream()
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
                ZipEntry entry = zipFile.getEntry(resource.getId());
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
}
