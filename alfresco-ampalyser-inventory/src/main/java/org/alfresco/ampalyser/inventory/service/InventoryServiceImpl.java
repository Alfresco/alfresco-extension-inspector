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

import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.alfresco.ampalyser.inventory.EntryProcessor;
import org.alfresco.ampalyser.inventory.model.InventoryReport;
import org.alfresco.ampalyser.inventory.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                Map<Resource.Type, List<Resource>> resources = entryProcessor
                    .processWarEntry(ze, zis);

                report.addResources(resources);

                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            try
            {
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                objectMapper.writeValue(new File(
                    "report.json"), report);
            }
            catch (IOException e)
            {
                logger.warn("Failed writing report to file " + report, e);
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
}
