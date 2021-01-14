/*
 * Copyright 2021 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfresco.extension_inspector.inventory.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.alfresco.extension_inspector.commons.InventoryUtils;
import org.alfresco.extension_inspector.inventory.EntryProcessor;
import org.alfresco.extension_inspector.inventory.output.InventoryOutput;
import org.alfresco.extension_inspector.model.InventoryReport;
import org.alfresco.extension_inspector.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class InventoryServiceImpl implements InventoryService
{
    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    @Autowired
    private EntryProcessor entryProcessor;

    @Override
    public InventoryReport extractInventoryReport(final String warPath)
    {
        try (final ZipInputStream zis = new ZipInputStream((new FileInputStream(warPath))))
        {
            logger.info("Starting war processing");

            final InventoryReport report = new InventoryReport();

            ZipEntry ze = zis.getNextEntry();
            while (ze != null)
            {
                if (ze.getName().endsWith("MANIFEST.MF"))
                {
                    Map<String, String> versions = InventoryUtils.parseManifestForVersion(ze, zis);
                    if (versions != null)
                    {
                        report.setAlfrescoVersion(versions.get(InventoryReport.IMPLEMENTATION_VERSION));
                    }
                }
                Map<Resource.Type, Set<Resource>> resources = entryProcessor.processWarEntry(ze, zis);
                report.addResources(resources);

                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            logger.info("War processing finished");

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

    public void generateInventoryReport(final String warPath, final InventoryOutput output)
    {
        InventoryReport report = extractInventoryReport(warPath);
        output.generateOutput(report);
    }
}
