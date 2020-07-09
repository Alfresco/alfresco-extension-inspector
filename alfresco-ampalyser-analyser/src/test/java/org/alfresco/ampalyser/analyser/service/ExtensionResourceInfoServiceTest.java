package org.alfresco.ampalyser.analyser.service;

import static org.alfresco.ampalyser.analyser.service.ExtensionResourceInfoService.compileClassDependenciesFromBytecode;
import static org.alfresco.ampalyser.commons.InventoryUtils.extract;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExtensionResourceInfoServiceTest
{
    @Mock
    private ConfigService configService;
    @InjectMocks
    private ExtensionResourceInfoService service;

    @Test
    void testCompileClassDependenciesFromBytecode() throws IOException
    {
        final Set<String> expected = Set.of(
            "com/fasterxml/jackson/databind/JsonNode.class",
            "com/fasterxml/jackson/databind/ObjectMapper.class",
            "com/fasterxml/jackson/databind/node/ObjectNode.class",
            "com/fasterxml/jackson/databind/node/TextNode.class",
            "com/google/common/collect/Maps.class",
            "com/jayway/jsonpath/DocumentContext.class",
            "com/jayway/jsonpath/Predicate.class",
            "org/alfresco/ai/rendition/strategy/textract/model/KeyValueSet.class",
            "org/alfresco/ai/rendition/textract/TextractParserUtils.class",
            "org/apache/commons/lang3/StringUtils.class",
            "org/slf4j/Logger.class",
            "org/slf4j/LoggerFactory.class"
        );

        try (final InputStream is = this.getClass().getResourceAsStream("/some-compiled.class.data"))
        {
            final Set<String> result = compileClassDependenciesFromBytecode(extract(is));
            assertNotNull(result);
            assertEquals(expected.size(), result.size());
            expected.forEach(e -> assertTrue(result.contains(e)));
        }
    }
}