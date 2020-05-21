/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.output;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.alfresco.ampalyser.inventory.model.InventoryReport;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONInventoryOutput implements InventoryOutput
{
    private static final Logger logger = LoggerFactory.getLogger(JSONInventoryOutput.class);

    private static final OutputType type = OutputType.JSON;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static
    {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private Path outputPath;

    public JSONInventoryOutput(String warPath, String outputPath) throws Exception
    {
        this.outputPath = getNormalizedPath(warPath, outputPath);
    }

    @Override
    public void generateOutput(InventoryReport report)
    {
        File reportFile = outputPath.toFile();
        try
        {
            FileUtils.touch(outputPath.toFile());

            objectMapper.writeValue(reportFile, report);

            if (logger.isInfoEnabled())
            {
                logger.info("Inventory report generated - " + reportFile.getAbsolutePath());
            }
        }
        catch (IOException e)
        {
            logger.error("Failed writing report to file " + reportFile.getAbsolutePath(), e);
        }
    }

    private Path getNormalizedPath(String warPath, String outputPath) throws Exception
    {
        if (outputPath == null)
        {
            outputPath = "";
        }
        Path path = Paths.get(outputPath);
        if (StringUtils.isEmpty(outputPath) ||
                FilenameUtils.getExtension(outputPath).isEmpty())
        {
            //use default inventory report name - <alfresco-war-name>.inventory.json
            String warFileName = FilenameUtils.getBaseName(warPath);
            String defaultPath = defaultPath(warFileName, type);
            path = path.resolve(defaultPath);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Output file - " + path);
        }

        return path;
    }

    public Path getOutputPath()
    {
        return outputPath;
    }

}
