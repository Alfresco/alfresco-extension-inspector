package org.alfresco.ampalyser.analyser.checker;

import static java.util.Collections.emptySet;
import static java.util.Map.entry;
import static java.util.stream.Collectors.toSet;
import static org.alfresco.ampalyser.model.Resource.Type.ALFRESCO_PUBLIC_API;
import static org.alfresco.ampalyser.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.CustomCodeConflict;
import org.alfresco.ampalyser.analyser.service.ConfigService;
import org.alfresco.ampalyser.analyser.service.ExtensionResourceInfoService;
import org.alfresco.ampalyser.model.AlfrescoPublicApiResource;
import org.alfresco.ampalyser.model.ClasspathElementResource;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomCodeCheckerTest
{
    private static final String OAA = "/org/alfresco/amp/";
    private static final String OAW = "/org/alfresco/war/";

    @Mock
    private ConfigService configService;
    @InjectMocks
    private ExtensionResourceInfoService extensionResourceInfoService = spy(ExtensionResourceInfoService.class);
    @InjectMocks
    private CustomCodeChecker checker;

    @Test
    void customCodeCheckerCompleteHappyFlowTest()
    {
        // AMP Classpath elements
        {
            doReturn(List.of(
                ampRes("to_be_or_not_to_be.nope"),
                ampRes("no_deps.class"),
                ampRes("deps_outside_alf.class"),
                ampRes("deps_in_amp.class"),
                ampRes("deps_to_alfresco_public_api.class"),
                ampRes("deps_to_core_alf_classes_which_is_baaad.class"),
                ampRes("deps_to_deprecated_alfresco_public_api_classes.class"),
                ampRes("deps_to_everything.class")
            )).when(configService).getExtensionResources(any());
        }

        final InventoryReport warInventory = new InventoryReport();
        {
            warInventory.setResources(Map.of(
                CLASSPATH_ELEMENT, List.of(
                    warRes("c1.class"),
                    warRes("c2.class")
                ),
                ALFRESCO_PUBLIC_API, List.of(
                    apar("c_APA_1ok", false),
                    apar("c_APA_2ok", false),
                    apar("c_APA_3deprecated", true),
                    apar("c_APA_4deprecated", true)
                )));
        }

        // AMP bytecode dependencies
        {
            doReturn(Map.ofEntries(
                // no dependencies -> no conflict
                entry(OAA + "no_deps.class", emptySet()),
                // dependencies outside alfresco
                entry(OAA + "deps_outside_alf.class",
                    Set.of("/hakuna/matata/jungle.class", "/nananana/batman.class")),
                // dependencies inside the amp -> no conflict
                entry(OAA + "deps_in_amp.class",
                    Set.of(OAA + "to_be_or_not_to_be.nope", OAA + "no_deps.class", OAA + "deps_outside_alf.class")),
                // dependencies to valid AlfrescoPublicApi classes -> no conflict
                entry(OAA + "deps_to_alfresco_public_api.class",
                    Set.of(OAW + "c_APA_1ok.class", OAW + "c_APA_2ok.class")),
                // dependencies in the war (not annotated) -> 1 conflict with 2 invalid dependencies
                entry(OAA + "deps_to_core_alf_classes_which_is_baaad.class",
                    Set.of(OAW + "c1.class", OAW + "c2.class")),
                // dependencies to deprecated AlfrescoPublicApi classes -> 1 conflict with 2 invalid dependencies
                entry(OAA + "deps_to_deprecated_alfresco_public_api_classes.class",
                    Set.of(OAW + "c_APA_3deprecated.class", OAW + "c_APA_4deprecated.class")),
                // dependencies to all kind of classes -> 1 conflict with multiple () invalid dependencies
                entry(OAA + "deps_to_everything.class",
                    Set.of(
                        "/hakuna/matata/jungle.class", "/nananana/batman.class",
                        OAA + "to_be_or_not_to_be.nope", OAA + "no_deps.class", OAA + "deps_outside_alf.class",
                        OAW + "c_APA_1ok.class", OAW + "c_APA_2ok.class",
                        OAW + "c1.class", OAW + "c2.class",
                        OAW + "c_APA_3deprecated.class", OAW + "c_APA_4deprecated.class"
                    ))
            )).when(extensionResourceInfoService).retrieveDependenciesPerClass();
        }

        final Set<Conflict> result = checker.process(warInventory, "6.0.0").collect(toSet());

        final Set<Conflict> expected = Set.of(
            conflict(ampRes("deps_to_core_alf_classes_which_is_baaad.class"),
                Set.of(OAW + "c1.class", OAW + "c2.class")),

            conflict(ampRes("deps_to_deprecated_alfresco_public_api_classes.class"),
                Set.of(OAW + "c_APA_3deprecated.class", OAW + "c_APA_4deprecated.class")),

            conflict(ampRes("deps_to_everything.class"),
                Set.of(
                    OAW + "c1.class", OAW + "c2.class",
                    OAW + "c_APA_3deprecated.class", OAW + "c_APA_4deprecated.class"
                )));
        assertEquals(expected.size(), result.size());
        expected.forEach(c -> assertTrue(result.contains(c)));
    }

    private static ClasspathElementResource ampRes(String id)
    {
        return new ClasspathElementResource(OAA + id, "definingObject");
    }

    private static Resource warRes(String id)
    {
        return new ClasspathElementResource(OAW + id, "definingObject");
    }

    private static Resource apar(String id, boolean deprecated)
    {
        return new AlfrescoPublicApiResource(OAW.substring(1) + id, deprecated);
    }

    private static Conflict conflict(ClasspathElementResource resource, Set<String> classes)
    {
        return new CustomCodeConflict(resource, classes, "6.0.0");
    }
}