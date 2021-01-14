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
package org.alfresco.extension_inspector.analyser.service;

import static org.alfresco.extension_inspector.analyser.service.ExtensionCodeAnalysisService.compileClassDependenciesFromBytecode;
import static org.alfresco.extension_inspector.commons.InventoryUtils.extract;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import org.alfresco.extension_inspector.analyser.util.BytecodeReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExtensionCodeAnalysisServiceTest
{
    @Mock
    private ConfigService configService;
    @Spy
    private BytecodeReader bytecodeReader;
    @InjectMocks
    private ExtensionCodeAnalysisService service;

    @Test
    void testCompileClassDependenciesFromBytecode() throws IOException
    {
        final Set<String> expected = Set.of(
            "/com/fasterxml/jackson/databind/JsonNode.class",
            "/com/fasterxml/jackson/databind/ObjectMapper.class",
            "/com/fasterxml/jackson/databind/node/ObjectNode.class",
            "/com/fasterxml/jackson/databind/node/TextNode.class",
            "/com/google/common/collect/Maps.class",
            "/com/jayway/jsonpath/DocumentContext.class",
            "/com/jayway/jsonpath/Predicate.class",
            "/org/alfresco/ai/rendition/strategy/textract/model/KeyValueSet.class",
            "/org/alfresco/ai/rendition/textract/TextractParserUtils.class",
            "/org/apache/commons/lang3/StringUtils.class",
            "/org/slf4j/Logger.class",
            "/org/slf4j/LoggerFactory.class"
        );

        try (final InputStream is = this.getClass().getResourceAsStream("/some-compiled.class.data"))
        {
            final Set<String> result = compileClassDependenciesFromBytecode("some-compiled.class", extract(is));
            assertNotNull(result);
            assertEquals(expected.size(), result.size());
            expected.forEach(e -> assertTrue(result.contains(e)));
        }
    }

    @Test
    void testRetrieveDependenciesPerClass_withJar() throws URISyntaxException
    {
        final String absoluteFilePath = Paths
            .get(getClass().getResource("/some.jar.data").toURI())
            .toFile().getAbsolutePath();

        doReturn(absoluteFilePath).when(configService).getExtensionPath();
        doReturn(bytecodeReader.readJarArtifact(absoluteFilePath)).when(bytecodeReader).readArtifact(any());

        final Map<String, Set<String>> someExpectedEntries = Map.of(
            "/org/alfresco/ampalyser/util/TestResource.class", Set.of(
                "/org/alfresco/ampalyser/util/TestResource.class"
            ),
            "/org/alfresco/ampalyser/command/CommandImpl.class", Set.of(
                "/org/alfresco/ampalyser/command/CommandImpl.class",
                "/org/alfresco/ampalyser/models/InventoryCommand.class",
                "/org/alfresco/ampalyser/command/Command.class",
                "/org/alfresco/ampalyser/command/CommandReceiver.class",
                "/org/alfresco/ampalyser/models/CommandOutput.class"
            ),
            "/org/alfresco/ampalyser/command/Command.class", Set.of(
                "/org/alfresco/ampalyser/command/Command.class",
                "/org/alfresco/ampalyser/models/CommandOutput.class"
            )
        );

        final Map<String, Set<String>> result = service.retrieveDependenciesPerClass();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(10, result.size());
        someExpectedEntries.forEach((k, v) -> {
            assertTrue(result.containsKey(k), "Failed at key " + k);
            assertEquals(v, result.get(k));
        });
    }

    @Test
    void testRetrieveDependenciesPerClass_withAmp() throws URISyntaxException
    {
        final String absoluteFilePath = Paths
            .get(getClass().getResource("/some.amp.data").toURI())
            .toFile().getAbsolutePath();

        doReturn(absoluteFilePath).when(configService).getExtensionPath();

        final Map<String, Set<String>> someExpectedEntries = Map.of(
            "/org/alfresco/integrations/google/docs/exceptions/MustUpgradeFormatException.class", Set.of(
                "/org/alfresco/integrations/google/docs/exceptions/MustUpgradeFormatException.class"
            ),
            "/org/alfresco/integrations/google/docs/exceptions/NotInGoogleDriveException.class", Set.of(
                "/org/alfresco/integrations/google/docs/exceptions/NotInGoogleDriveException.class",
                "/org/alfresco/service/cmr/repository/NodeRef.class",
                "/org/alfresco/integrations/google/docs/exceptions/GoogleDocsServiceException.class"
            ),
            "/org/alfresco/integrations/google/docs/GoogleDocsModel.class", Set.of(
                "/org/alfresco/integrations/google/docs/GoogleDocsModel.class",
                "/org/alfresco/service/namespace/QName.class"
            )
        );

        final Map<String, Set<String>> result = service.retrieveDependenciesPerClass();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(9, result.size());
        someExpectedEntries.forEach((k, v) -> {
            assertTrue(result.containsKey(k), "Failed at key " + k);
            assertEquals(v, result.get(k));
        });
    }

    @Test
    void retrieveAllDependencies_withJar() throws URISyntaxException
    {
        final String absoluteFilePath = Paths
            .get(getClass().getResource("/some.jar.data").toURI())
            .toFile().getAbsolutePath();

        doReturn(absoluteFilePath).when(configService).getExtensionPath();
        doReturn(bytecodeReader.readJarArtifact(absoluteFilePath)).when(bytecodeReader).readArtifact(any());

        final Set<String> result = service.retrieveAllDependencies();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("/org/alfresco/ampalyser/models/InventoryCommand.class"));
        assertEquals(21, result.size());
    }

    @Test
    void retrieveAllDependencies_withAmp() throws URISyntaxException
    {
        final String absoluteFilePath = Paths
            .get(getClass().getResource("/some.amp.data").toURI())
            .toFile().getAbsolutePath();

        doReturn(absoluteFilePath).when(configService).getExtensionPath();

        final Set<String> result = service.retrieveAllDependencies();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("/org/alfresco/service/namespace/QName.class"));
        assertEquals(37, result.size());
    }
}