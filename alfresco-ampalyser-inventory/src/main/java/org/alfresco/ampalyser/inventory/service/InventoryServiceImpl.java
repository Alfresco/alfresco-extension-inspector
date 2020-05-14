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
import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.alfresco.ampalyser.inventory.EntryProcessor;
import org.alfresco.ampalyser.inventory.model.AlfrescoPublicApiResource;
import org.alfresco.ampalyser.inventory.model.BeanResource;
import org.alfresco.ampalyser.inventory.model.ClasspathElementResource;
import org.alfresco.ampalyser.inventory.model.FileResource;
import org.alfresco.ampalyser.inventory.model.InventoryReport;
import org.alfresco.ampalyser.inventory.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private EntryProcessor entryProcessor;

    @Override
    public InventoryReport extractInventoryReport(String warPath)
    {
        ZipInputStream zis = openWarFile(warPath);
        if (zis == null)
            return null;

        InventoryReport report = new InventoryReport();

        try
        {
            ZipEntry ze = zis.getNextEntry();
            System.out.println("Starting war processing");
            while (ze != null)
            {
                Map<Resource.Type, List<Resource>> resources = entryProcessor
                    .processWarEntry(ze, zis);

                addResultsToReport(resources, report);

                zis.closeEntry();
                ze = zis.getNextEntry();
            }
        }
        catch (IOException e)
        {
            System.err.println("Failed reading web archive " + warPath);
            e.printStackTrace(System.err);
        }
        finally
        {
            try
            {
                zis.close();
            }
            catch (Exception e)
            {
                //
            }
        }
        try
        {

            objectMapper.writeValue(new File(
                "report.json"), report);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return report;
    }

    private static void addResultsToReport(Map<Resource.Type, List<Resource>> resources,
        InventoryReport report)
    {
        for (Resource.Type type: resources.keySet())
        {
            switch (type){
            case FILE:
                List<FileResource> fileResourceList = objectMapper
                    .convertValue(resources.get(Resource.Type.FILE),
                        new TypeReference<List<FileResource>>()
                        {
                        });
                report.addFiles(fileResourceList);
                break;
            case CLASSPATH_ELEMENT:
                List<ClasspathElementResource> classpathResourceList = objectMapper
                    .convertValue(resources.get(Resource.Type.CLASSPATH_ELEMENT),
                        new TypeReference<List<ClasspathElementResource>>()
                        {
                        });
                report.addClasspathElements(classpathResourceList);
                break;
            case ALFRESCO_PUBLIC_API:
                List<AlfrescoPublicApiResource> apiResourceList = objectMapper
                    .convertValue(resources.get(Resource.Type.ALFRESCO_PUBLIC_API),
                        new TypeReference<List<AlfrescoPublicApiResource>>()
                        {
                        });
                report.addAlfrescoPublicApis(apiResourceList);
                break;
            case BEAN:
                List<BeanResource> beanResourceList = objectMapper
                    .convertValue(resources.get(Resource.Type.BEAN),
                        new TypeReference<List<BeanResource>>()
                        {
                        });
                report.addBeans(beanResourceList);
                break;
            default:
                LOGGER.info("Unknown resource type '" + type + "' won't be added to inventory report");
            }
        }
    }

    //TODO improve error handling
    private ZipInputStream openWarFile(String warPath)
    {
        FileInputStream fis;
        try
        {
            fis = new FileInputStream(warPath);
        }
        catch (Exception e)
        {
            System.err.println("Failed opening file " + warPath);
            e.printStackTrace(System.err);
            return null;
        }
        ZipInputStream zis;
        try
        {
            zis = new ZipInputStream(fis);
        }
        catch (Exception e)
        {
            try
            {
                fis.close();
            }
            catch (Exception ee)
            {
                //
            }
            System.err.println("Failed opening web archive " + warPath);
            e.printStackTrace(System.err);
            return null;
        }
        return zis;
    }
}
