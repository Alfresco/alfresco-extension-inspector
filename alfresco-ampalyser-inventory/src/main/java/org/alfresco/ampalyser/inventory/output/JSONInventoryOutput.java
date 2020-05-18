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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONInventoryOutput implements InventoryOutput
{
    private static final Logger logger = LoggerFactory.getLogger(JSONInventoryOutput.class);

    private static final OutputType type = OutputType.JSON;
    private static final ObjectMapper objectMapper = new ObjectMapper();

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

            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(reportFile, report);

            logger.info("Inventory report generated - " + reportFile.getAbsolutePath());
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

        logger.debug("Output file - " + path);

        return path;
    }
}
