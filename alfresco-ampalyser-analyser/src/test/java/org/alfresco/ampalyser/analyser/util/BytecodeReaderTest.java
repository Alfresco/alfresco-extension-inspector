package org.alfresco.ampalyser.analyser.util;

import static java.util.Collections.emptyMap;
import static org.alfresco.ampalyser.analyser.util.BytecodeReader.extractClassBytecodeFromJar;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BytecodeReaderTest
{
    @Spy
    private BytecodeReader bytecodeReader;

    @Test
    public void testReadArtifactWithJar()
    {
        doReturn(emptyMap()).when(bytecodeReader).readJarArtifact(any());

        bytecodeReader.readArtifact("something.jar");

        verify(bytecodeReader, times(1)).readJarArtifact(any());
    }

    @Test
    public void testReadArtifactWithAmp()
    {
        doReturn(emptyMap()).when(bytecodeReader).readAmpArtifact(any());

        bytecodeReader.readArtifact("something.amp");

        verify(bytecodeReader, times(1)).readAmpArtifact(any());
    }

    @Test
    public void testReadJarArtifact() throws URISyntaxException
    {
        final String absoluteFilePath = Paths
            .get(getClass().getResource("/some.jar.data").toURI())
            .toFile().getAbsolutePath();

        final Map<String, List<byte[]>> result = bytecodeReader.readJarArtifact(absoluteFilePath);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        result.forEach((k, v) -> {
            assertTrue(k.endsWith(".class"));
            assertEquals(1, v.size());
        });
    }

    @Test
    public void testReadAmpArtifact() throws URISyntaxException
    {
        final String absoluteFilePath = Paths
            .get(getClass().getResource("/some.amp.data").toURI())
            .toFile().getAbsolutePath();

        final Map<String, Integer> expected = Map.of(
            "/org/alfresco/integrations/google/docs/GoogleDocsModel.class", 1,
            "/org/alfresco/integrations/google/docs/GoogleDocsConstants.class", 1,
            "/org/alfresco/integrations/google/docs/utils/FileNameUtil.class", 2,
            "/org/alfresco/integrations/google/docs/utils/FileRevisionComparator.class", 1,
            "/org/alfresco/integrations/google/docs/model/EditingInGoogleAspect.class", 1,
            "/org/alfresco/integrations/google/docs/exceptions/ConcurrentEditorException.class", 1,
            "/org/alfresco/integrations/google/docs/exceptions/MustDowngradeFormatException.class", 1,
            "/org/alfresco/integrations/google/docs/exceptions/MustUpgradeFormatException.class", 1,
            "/org/alfresco/integrations/google/docs/exceptions/NotInGoogleDriveException.class", 1
        );

        final Map<String, List<byte[]>> result = bytecodeReader.readAmpArtifact(absoluteFilePath);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expected.size(), result.size());
        expected.forEach((k, v) -> {
            assertTrue(result.containsKey(k), "Can't find entry for " + k);
            assertEquals(v, result.get(k).size());
        });
    }

    @Test
    public void testExtractClassBytecodeFromJar() throws IOException
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