/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;

public class AsmDependencyVisitorTest
{
    @Test
    public void testAsmClassReader() throws IOException
    {
        final Set<String> expected = Set.of(
            "com/fasterxml/jackson/databind/JsonNode",
            "com/fasterxml/jackson/databind/ObjectMapper",
            "com/fasterxml/jackson/databind/node/ObjectNode",
            "com/fasterxml/jackson/databind/node/TextNode",
            "com/google/common/collect/Maps",
            "com/jayway/jsonpath/DocumentContext",
            "com/jayway/jsonpath/Predicate",
            "java/io/IOException",
            "java/lang/CharSequence",
            "java/lang/Class",
            "java/lang/Double",
            "java/lang/Object",
            "java/lang/String",
            "java/lang/invoke/CallSite",
            "java/lang/invoke/LambdaMetafactory",
            "java/lang/invoke/MethodHandle",
            "java/lang/invoke/MethodHandles$Lookup",
            "java/lang/invoke/MethodType",
            "java/util/ArrayList",
            "java/util/HashMap",
            "java/util/List",
            "java/util/Map",
            "java/util/Map$Entry",
            "java/util/Objects",
            "java/util/Set",
            "java/util/function/Consumer",
            "java/util/function/Function",
            "java/util/function/Predicate",
            "java/util/stream/Collector",
            "java/util/stream/Collectors",
            "java/util/stream/Stream",
            "org/alfresco/ai/rendition/strategy/textract/model/KeyValueSet",
            "org/alfresco/ai/rendition/textract/TextractParserUtils",
            "org/apache/commons/lang3/StringUtils",
            "org/slf4j/Logger",
            "org/slf4j/LoggerFactory"
        );

        try (final InputStream is = this.getClass().getResourceAsStream("/some-compiled.class.data"))
        {
            final ClassReader reader = new ClassReader(is);
            final DependencyVisitor visitor = new DependencyVisitor();

            reader.accept(visitor, ClassReader.EXPAND_FRAMES);
            visitor.visitEnd();

            final Set<String> result = visitor.getClasses();
            assertNotNull(result);
            assertEquals(expected.size(), result.size());
            expected.forEach(e -> assertTrue(result.contains(e), "Missing: " + e));
        }
    }
}
