/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.extension_inspector.inventory;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import org.alfresco.extension_inspector.inventory.output.JSONInventoryOutput;
import org.alfresco.extension_inspector.model.InventoryReport;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

public class InventoryReportTest
{
    @Test
    public void testDefaultOutputPath1()
    {
        String warPath = "alfresco.war";
        String outputPath = "";
        JSONInventoryOutput output = new JSONInventoryOutput(warPath, outputPath);

        assertEquals("alfresco.inventory.json", output.getOutputPath().toString());
    }

    @Test
    public void testDefaultOutputPath2()
    {
        String warPath = "alfresco-6.1.0.war";
        String outputPath = null;
        JSONInventoryOutput output = new JSONInventoryOutput(warPath, outputPath);

        assertEquals("alfresco-6.1.0.inventory.json", output.getOutputPath().toString());
    }

    @Test
    public void testDefaultOutputPath3()
    {
        String warPath = "alfresco-6.2.war";
        String outputPath = "   ";
        JSONInventoryOutput output = new JSONInventoryOutput(warPath, outputPath);

        assertEquals("alfresco-6.2.inventory.json", output.getOutputPath().toString());
    }

    @Test
    public void testGivenOutputPath1()
    {
        String warPath = "alfresco.war";
        String outputPath = "report.json";
        JSONInventoryOutput output = new JSONInventoryOutput(warPath, outputPath);

        assertEquals(outputPath, output.getOutputPath().toString());
    }

    @Test
    public void testGivenOutputPath2()
    {
        String warPath = "alfresco.war";
        String outputPath = "out/report.json";
        JSONInventoryOutput output = new JSONInventoryOutput(warPath, outputPath);

        assertEquals(Path.of(outputPath).toString(), output.getOutputPath().toString());
    }

    @Test
    public void testGivenOutputFolderPath1()
    {
        String warPath = "alfresco.war";
        String outputPath = "out/folder";
        JSONInventoryOutput output = new JSONInventoryOutput(warPath, outputPath);

        assertEquals(Path.of(outputPath).toString() + separator + "alfresco.inventory.json", output.getOutputPath().toString());
    }

    @Test
    public void testGivenOutputFolderPath2()
    {
        String warPath = "alfresco.war";
        String outputPath = "/out//folder/";

        JSONInventoryOutput output = new JSONInventoryOutput(warPath, outputPath);

        // Build expected output path
        String expectedOutputPath = Path.of(outputPath).toString() + separator + "alfresco.inventory.json";

        assertEquals(expectedOutputPath, output.getOutputPath().toString());
    }

    @Test
    public void testGivenOutputPathWithJson()
    {
        String warPath = "alfresco.war";
        String outputPath = "/out//folder/report.JSon";
        JSONInventoryOutput output = new JSONInventoryOutput(warPath, outputPath);

        assertEquals(Path.of(outputPath).toString(), output.getOutputPath().toString());
    }

    @Test
    public void testGivenOutputPathWithNonJsonExtension()
    {
        String warPath = "alfresco.war";
        String outputPath = "out/folder/report.myextension";
        JSONInventoryOutput output = new JSONInventoryOutput(warPath, outputPath);

        assertEquals(Path.of(outputPath).toString(), output.getOutputPath().toString());

        output.generateOutput(new InventoryReport());

        File reportFile = output.getOutputPath().toFile();
        assertTrue(reportFile.exists());
        assertTrue(FileUtils.sizeOf(reportFile) > 0);

        reportFile.delete();
    }

    @Test
    public void testInvalidOutputPath()
    {
        String warPath = "alfresco.war";
        // '\u0000' - nul character
        String outputPath = "out"+'\u0000'+"report.json";
        try
        {
            JSONInventoryOutput output = new JSONInventoryOutput(warPath, outputPath);
            fail(" The output path " + outputPath + " is not valid");
        }
        catch(InvalidPathException ipe)
        {
            //expected
        }
    }

/*
    The report is created on Windows when given absolute path

    @Test
    public void testReportNotGenerated() throws Exception
    {
        String warPath = "alfresco.war";
        String outputPath = "/out/report.json";
        JSONInventoryOutput output = new JSONInventoryOutput(warPath, outputPath);

        assertEquals(Path.of(outputPath).toString(), output.getOutputPath().toString());

        output.generateOutput(new InventoryReport());

        File reportFile = output.getOutputPath().toFile();
        // The output path /out/report.json is an absolute path and cannot be created
        assertFalse(reportFile.exists(), outputPath + " can't be created");
    }*/

    @Test
    public void testReportIsGenerated1()
    {
        String warPath = "alfresco.war";
        String outputPath = "target";
        JSONInventoryOutput output = new JSONInventoryOutput(warPath, outputPath);

        assertEquals("target" + separator + "alfresco.inventory.json", output.getOutputPath().toString());

        //Generate the report
        output.generateOutput(new InventoryReport());
        File reportFile = output.getOutputPath().toFile();
        // Check if the report exists
        assertTrue(reportFile.exists());
        assertTrue(FileUtils.sizeOf(reportFile) > 0);

        reportFile.delete();
    }

    @Test
    public void testReportIsGenerated2()
    {
        String warPath = "alfresco.war";
        String outputPath = "target/aa#/&%()+-//report.JSon";
        JSONInventoryOutput output = new JSONInventoryOutput(warPath, outputPath);

        assertEquals(Path.of(outputPath).toString(), output.getOutputPath().toString());
        output.generateOutput(new InventoryReport());

        File reportFile = output.getOutputPath().toFile();
        assertTrue(reportFile.exists());

        reportFile.delete();
    }
}
