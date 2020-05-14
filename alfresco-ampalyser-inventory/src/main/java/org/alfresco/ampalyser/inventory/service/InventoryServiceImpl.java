/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.alfresco.ampalyser.inventory.EntryProcessor;
import org.alfresco.ampalyser.inventory.model.InventoryReport;
import org.alfresco.ampalyser.inventory.model.Resource;
import org.alfresco.ampalyser.inventory.utils.InventoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Service
public class InventoryServiceImpl implements InventoryService
{
    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private EntryProcessor entryProcessor;

    @Override
    public InventoryReport extractInventoryReport(final String warPath)
    {
        try (final ZipInputStream zis = new ZipInputStream((new FileInputStream(warPath))))
        {
            final InventoryReport report = new InventoryReport();

            ZipEntry ze = zis.getNextEntry();
            System.out.println("Starting war processing");
            while (ze != null)
            {
                Map<Resource.Type, List<Resource>> resources = entryProcessor.processWarEntry(ze, zis);
                report.addResources(resources);

                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            return report;
        }
        catch (FileNotFoundException e)
        {
            logger.error("Failed opening file " + warPath, e);
            throw new IllegalArgumentException("Failed to open file " + warPath, e);
        }
        catch (IOException e)
        {
            logger.error("Failed reading web archive " + warPath, e);
            throw new RuntimeException("IO error while reading archive " + warPath, e);
        }
    }

    public void generateInventoryReport(final String warPath, final String outputPath)
    {
        Path path = Paths.get(outputPath);
        try
        {
            Files.createDirectories(path.toAbsolutePath().getParent());
            Files.createFile(path);

            InventoryReport report = extractInventoryReport(warPath);

            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(path.toFile(), report);

            logger.warn("Inventory report - " + path.toAbsolutePath());
        }
        catch (IOException e)
        {
            logger.error("Failed writing report to file " + path.toAbsolutePath(), e);
        }
    }

}
