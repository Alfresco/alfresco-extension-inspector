package org.alfresco.extension_inspector.analyser.checker;

import static java.util.Collections.emptySet;
import static java.util.Map.entry;
import static java.util.stream.Collectors.toSet;
import static org.alfresco.extension_inspector.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.Map;
import java.util.Set;

import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.result.WarLibraryUsageConflict;
import org.alfresco.extension_inspector.analyser.service.ConfigService;
import org.alfresco.extension_inspector.analyser.service.ExtensionCodeAnalysisService;
import org.alfresco.extension_inspector.analyser.service.ExtensionResourceInfoService;
import org.alfresco.extension_inspector.model.ClasspathElementResource;
import org.alfresco.extension_inspector.model.InventoryReport;
import org.alfresco.extension_inspector.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WarLibraryUsageCheckerTest
{
    @Mock
    private ConfigService configService;
    @InjectMocks
    private ExtensionResourceInfoService extensionResourceInfoService = spy(ExtensionResourceInfoService.class);
    @Spy
    private ExtensionCodeAnalysisService extensionCodeAnalysisService;
    @InjectMocks
    private WarLibraryUsageChecker checker;

    @Test
    void processInternal()
    {
        // AMP Classpath elements
        {
            doReturn(Set.of(
                res("/com/example/test/A1.nope", "white"),
                res("/com/example/test/A1.class", "white"),
                res("/com/example/test/A2.class", "white"),
                res("/com/example/test/A3.class", "white"),
                res("/com/example/test/A4.class", "white"),
                res("/com/example/test/A1.class", "black"),
                res("/com/example/test/A2.class", "black"),
                res("/com/example/test/A7.class", "black"),
                res("/com/example/test/A8.class", "black"),
                res("/com/example/test/A9.class", "black"),
                res("/com/example/test/C0.class", "color"),
                res("/com/example/test/C00.class", "color"),
                res("/com/example/test/C10.class", "color"),
                res("/com/example/test/C11.class", "color"),
                res("/com/example/test/C12.class", "color"),
                res("/com/example/test/C20.class", "color"),
                res("/com/example/test/C21.class", "color"),
                res("/com/example/test/C22.class", "color"),
                res("/com/example/test/C23.class", "color"),
                res("/com/example/test/C24.class", "color"),
                res("/com/example/test/C25.class", "color"),
                res("/com/example/test/C31.class", "color"),
                res("/com/example/test/C32.class", "color"),
                res("/com/example/test/C40.class", "color"),
                res("/com/example/test/C41.class", "color"),
                res("/com/example/test/C42.class", "color"),
                res("/com/example/test/C50.class", "color"),
                res("/com/example/test/C51.class", "color"),
                res("/com/example/test/C52.class", "color"),
                res("/com/example/test/C53.class", "color"),
                res("/com/example/test/C54.class", "color"),
                res("/com/example/test/C55.class", "color"),
                res("/com/example/test/C56.class", "color"),
                res("/com/example/test/C57.class", "color"),
                res("/com/example/test/C60.class", "color"),
                res("/com/example/test/C61.class", "color"),
                res("/com/example/test/C62.class", "color"),
                res("/com/example/test/C63.class", "color")
            )).when(configService).getExtensionResources(any());
        }

        final InventoryReport warInventory = new InventoryReport();
        {
            warInventory.setResources(Map.of(CLASSPATH_ELEMENT, Set.of(
                res("/com/example/test/W10.class", "red"),
                res("/com/example/test/W11.class", "red"),
                res("/com/example/test/W12.class", "red"),
                res("/com/example/test/W13.class", "red"),
                res("/com/example/test/W14.class", "red"),

                res("/com/example/test/W13.class", "blue"),
                res("/com/example/test/W14.class", "blue"),
                res("/com/example/test/W15.class", "blue"),
                res("/com/example/test/W16.class", "blue"),
                res("/com/example/test/W17.class", "blue"),
                res("/com/example/test/W18.class", "blue"),
                res("/com/example/test/W19.class", "blue"),
                res("/com/example/test/W20.class", "blue"),
                res("/com/example/test/W21.class", "blue"),
                res("/com/example/test/W22.class", "blue"),

                res("/org/alfresco/W2.class", "alf"),
                res("/org/alfresco/W3.class", "alf"),
                res("/org/alfresco/W4.class", "alf"),

                res("/com/example/test/W10.nope", "alf"),
                res("/com/example/test/W11.nope", "alf"),
                res("/org/alfresco/W3.nope", "alf")
            )));
        }

        // AMP bytecode dependencies
        {
            doReturn(Map.ofEntries(
                // no dependencies
                entry("/com/example/test/C0.class", emptySet()),

                // dependencies not in AMP or WAR
                entry("/com/example/test/C11.class", Set.of("/nope/Nope.class")),

                // only AMP dependencies
                entry("/com/example/test/C21.class", Set.of("/com/example/test/A1.class")),
                entry("/com/example/test/C22.class", Set.of("/com/example/test/A1.class", "/nope/Nope.class")),
                entry("/com/example/test/C23.class", Set.of("/com/example/test/A1.class", "/com/example/test/A2.class")),
                entry("/com/example/test/C24.class", Set.of("/com/example/test/A1.class", "/com/example/test/A7.class")),
                entry("/com/example/test/C25.class", Set.of("/com/example/test/A8.class")),

                // Dependencies not in the WAR
                entry("/com/example/test/C31.class", Set.of("/org/alfresco/C0.class")),
                entry("/com/example/test/C32.class", Set.of("/org/alfresco/C0.class", "/org/alfresco/C1.class")),

                // Dependencies in the WAR
                entry("/com/example/test/C41.class", Set.of("/org/alfresco/w2.class")),
                entry("/com/example/test/C42.class", Set.of("/org/alfresco/w2.class", "/org/alfresco/W3.class")),

                entry("/com/example/test/C51.class", Set.of("/com/example/test/W10.class")),
                entry("/com/example/test/C52.class", Set.of("/com/example/test/W17.class")),

                entry("/com/example/test/C53.class", Set.of("/com/example/test/W13.class")),

                entry("/com/example/test/C54.class", Set.of("/com/example/test/W11.class", "/com/example/test/W15.class")),

                entry("/com/example/test/C55.class", Set.of("/com/example/test/W19.class", "/org/alfresco/W4.class")),
                entry("/com/example/test/C56.class", Set.of("/com/example/test/W20.class", "/some/Other.class")),

                entry("/com/example/test/A2.class", Set.of("/com/example/test/W21.class")),

                // Dependencies in AMP and WAR
                entry("/com/example/test/C61.class", Set.of("/com/example/test/A2.class", "/com/example/test/W22.class"))
            )).when(extensionCodeAnalysisService).retrieveDependenciesPerClass();
        }

        final Set<Conflict> result = checker.process(warInventory, "6.0.0").collect(toSet());

        final Set<Conflict> expected = Set.of(
            conflict(res("/com/example/test/C51.class", "color"), Set.of(new ClasspathElementResource(
                "/com/example/test/W10.class", "test.jar"))),
            conflict(res("/com/example/test/C52.class", "color"), Set.of(new ClasspathElementResource("/com/example/test/W17.class", "test.jar"))),
            conflict(res("/com/example/test/C53.class", "color"), Set.of(new ClasspathElementResource("/com/example/test/W13.class", "test.jar"))),
            conflict(res("/com/example/test/C54.class", "color"), Set.of(new ClasspathElementResource("/com/example/test/W11.class", "test.jar"), 
                new ClasspathElementResource("/com/example/test/W15.class", "test.jar"))),
            conflict(res("/com/example/test/C55.class", "color"), Set.of(new ClasspathElementResource("/com/example/test/W19.class", "test.jar"))),
            conflict(res("/com/example/test/C56.class", "color"), Set.of(new ClasspathElementResource("/com/example/test/W20.class", "test.jar"))),
            conflict(res("/com/example/test/A2.class", "white"), Set.of(new ClasspathElementResource("/com/example/test/W21.class", "test.jar"))),
            conflict(res("/com/example/test/A2.class", "black"), Set.of(new ClasspathElementResource("/com/example/test/W21.class", "test.jar"))),
            conflict(res("/com/example/test/C61.class", "color"), Set.of(new ClasspathElementResource("/com/example/test/W22.class", "test.jar")))
        );
        assertEquals(expected.size(), result.size());
        expected.forEach(c -> assertTrue(expected.contains(c)));
    }


    @Test
    void processInternalWithAllowedList()
    {
        // AMP Classpath elements
        {
            doReturn(Set.of(
                res("/com/example/amp/A1.class", "white"),
                res("/com/example/amp/A2.class", "white"),
                res("/com/example/amp/A3.class", "white"),
                res("/com/example/amp/A4.class", "white")
            )).when(configService).getExtensionResources(any());
        }

        final InventoryReport warInventory = new InventoryReport();
        {
            warInventory.setResources(Map.of(CLASSPATH_ELEMENT, Set.of(
                res("/com/example/abc/X1.class", "red"),
                res("/com/example/abc/X2.class", "red"),
                res("/com/example/def/X3.class", "red"),
                res("/com/example/def/X33.class", "red"),
                res("/com/example/def/X4.class", "red")
            )));
        }

        // AMP bytecode dependencies
        {
            doReturn(Map.ofEntries(
                entry("/com/example/amp/A1.class", Set.of("/com/example/abc/X1.class")),
                entry("/com/example/amp/A2.class", Set.of("/com/example/abc/X2.class", "/nope/Nope.class")),
                entry("/com/example/amp/A3.class", Set.of("/com/example/def/X3.class", "/com/example/abc/X2.class")),
                entry("/com/example/amp/A4.class", Set.of("/com/example/def/X4.class", "/com/example/test/A7.class"))
            )).when(extensionCodeAnalysisService).retrieveDependenciesPerClass();
        }

        doReturn(Set.of("com/example/def")).when(configService).getThirdPartyAllowedList();
        final Set<Conflict> result = checker.process(warInventory, "6.0.0").collect(toSet());

        final Set<Conflict> expected = Set.of(
            conflict(res("/com/example/amp/A3.class", "color"), Set.of(new ClasspathElementResource("/com/example/abc/X2.class", "test.jar"))),
            conflict(res("/com/example/amp/A2.class", "color"), Set.of(new ClasspathElementResource("/com/example/abc/X2.class", "test.jar"))),
            conflict(res("/com/example/amp/A1.class", "color"), Set.of(new ClasspathElementResource("/com/example/abc/X1.class", "test.jar")))
        );
        assertEquals(expected.size(), result.size());
        expected.forEach(c -> assertTrue(expected.contains(c)));
    }

    private static ClasspathElementResource res(String id, String definingObject)
    {
        return new ClasspathElementResource(id, definingObject);
    }

    private static WarLibraryUsageConflict conflict(ClasspathElementResource resource, Set<Resource> classes)
    {
        return new WarLibraryUsageConflict(resource, classes, "6.0.0");
    }
}