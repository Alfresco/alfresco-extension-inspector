/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.service;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.alfresco.ampalyser.inventory.EntryProcessor;
import org.alfresco.ampalyser.inventory.model.InventoryReport;
import org.alfresco.ampalyser.inventory.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService
{
    @Autowired
    private EntryProcessor entryProcessor;

    @Override
    public InventoryReport extractInventoryReport(String warPath)
    {
        ZipInputStream zis = openWarFile(warPath);
        if (zis == null)
            return null;

        InventoryReport report = null;
        try
        {
            ZipEntry ze = zis.getNextEntry();
            System.out.println("Starting war processing");
            while (ze != null)
            {
                Map<Resource.Type, List<Resource>> resources = entryProcessor
                    .processWarEntry(ze, zis);

                //TODO add found resources to inventory report

                zis.closeEntry();
                ze = zis.getNextEntry();
            }
        }
        catch (Exception e)
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
        return report;
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
