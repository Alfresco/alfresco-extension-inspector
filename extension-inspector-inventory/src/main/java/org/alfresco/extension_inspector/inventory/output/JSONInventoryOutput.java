/*
 * Copyright 2021 Alfresco Software, Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.alfresco.extension_inspector.inventory.output;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.alfresco.extension_inspector.model.InventoryReport;
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

    public JSONInventoryOutput(String warPath, String outputPath)
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

    private Path getNormalizedPath(String warPath, String outputPath)
    {
        if (outputPath == null)
        {
            outputPath = "";
        }
        outputPath = outputPath.trim();
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
