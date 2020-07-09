package org.alfresco.ampalyser.analyser.util;

import static org.alfresco.ampalyser.analyser.util.BytecodeReader.extractClassBytecodeFromJar;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

class BytecodeReaderTest
{

    @Test
    void testExtractClassBytecodeFromJar() throws IOException
    {
        final Set<String> expected = Set.of(
            "/org/alfresco/ampalyser/models/InventoryCommand.class",
            "/org/alfresco/ampalyser/command/CommandReceiver.class",
            "/org/alfresco/ampalyser/command/CommandImpl.class",
            "/org/alfresco/ampalyser/AmpalyserClient.class",
            "/org/alfresco/ampalyser/models/CommandOutput.class",
            "/org/alfresco/ampalyser/command/CommandExecutor.class",
            "/org/alfresco/ampalyser/util/TestResource.class",
            "/org/alfresco/ampalyser/util/AppConfig.class",
            "/org/alfresco/ampalyser/util/JsonInventoryParser.class",
            "/org/alfresco/ampalyser/command/Command.class"
        );
        try (InputStream inputStream = new BufferedInputStream(getClass().getResourceAsStream("/some.jar.data")))
        {
            final Map<String, byte[]> result = extractClassBytecodeFromJar(inputStream);
            assertNotNull(result);
            assertEquals(expected.size(), result.size());
            expected.forEach(e -> assertTrue(result.containsKey(e)));
        }
    }
}