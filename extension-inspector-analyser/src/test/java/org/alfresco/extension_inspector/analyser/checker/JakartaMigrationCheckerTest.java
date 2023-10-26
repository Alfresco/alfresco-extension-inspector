/*
 * Copyright 2023 Alfresco Software, Ltd.
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
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;

import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.result.JakartaMigrationConflict;
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

@ExtendWith (MockitoExtension.class)
public class JakartaMigrationCheckerTest
{
    private static final String OAA = "/org/alfresco/amp/";
    private static final String JAVAX_MAIL = "/javax/mail/";

    @Mock
    private ConfigService configService;
    @InjectMocks
    private ExtensionResourceInfoService extensionResourceInfoService = spy(ExtensionResourceInfoService.class);
    @Spy
    private ExtensionCodeAnalysisService extensionCodeAnalysisService;
    @InjectMocks
    private JakartaMigrationChecker checker;

    @Test
    void jakartaMigrationConflictCheckerCompleteHappyFlowTest()
    {
        when(configService.getJakartaMigrationClassList()).thenReturn(Set.of("javax/mail","jakarta/mail"));

        // AMP Classpath elements
        {
            doReturn(Set.of(
                    ampRes("no_dependencies.class"),
                    ampRes("not_using_javax_or_jakarta_dependency.class"),
                    ampRes("using_javax_dependency_not_in_migration_list.class"),
                    ampRes("using_jakarta_dependency_not_in_migration_list.class"),
                    ampRes("using_dependency_in_migration_list_and_in_acs.class"),
                    ampRes("using_dependency_in_migration_list_but_not_in_acs.class")
                           )).when(configService).getExtensionResources(any());
        }

        final InventoryReport warInventory = new InventoryReport();
        {
            warInventory.setResources(Map.of(
                    CLASSPATH_ELEMENT, Set.of(
                            warRes("mail.class"),
                            warRes("someother.class")
                            )));
        }

        // AMP bytecode dependencies
        {
            doReturn(Map.ofEntries(
                    // no dependencies -> no conflict
                    entry(OAA + "no_dependencies.class", emptySet()),
                    // not using jakarta migration related dependencies  -> no conflict
                    entry(OAA + "not_using_javax_or_jakarta_dependency.class",
                            Set.of("/some/unrelated/dep1.class", "/another/unrelated/dep2.class")),
                    // using javax dependency which is not in the jakarta migration class list  -> no conflict
                    entry(OAA + "using_javax_dependency_not_in_migration_list.class",
                            Set.of("/javax/notinlist/dep3.class", "/some/unrelated/dep1.class")),
                    // using jakarta dependency which is not in the jakarta migration class list  -> no conflict
                    entry(OAA + "using_jakarta_dependency_not_in_migration_list.class",
                            Set.of("/jakarta/notinlist/dep4.class", "/some/unrelated/dep1.class")),
                    // using dependency which is in the jakarta migration class list and used in ACS -> no conflict
                    entry(OAA + "using_dependency_in_migration_list_and_in_acs.class",
                            Set.of("/javax/mail/mail.class", "/some/unrelated/dep1.class")),
                    // using dependency which is in the jakarta migration class list and not used in ACS
                    entry(OAA + "using_dependency_in_migration_list_but_not_in_acs.class",
                            Set.of("/jakarta/mail/notmail.class", "/some/unrelated/dep1.class"))
                    )).when(extensionCodeAnalysisService).retrieveDependenciesPerClass();
        }

        final Set<Conflict> result = checker.process(warInventory, "7.4.1").collect(toSet());

        final Set<Conflict> expected = Set.of(
                conflict(ampRes("using_dependency_in_migration_list_but_not_in_acs.class"),
                        Set.of("jakarta.mail.notmail")));
        assertEquals(expected.size(), result.size());
        expected.forEach(c -> assertTrue(result.contains(c)));
    }

    private static ClasspathElementResource ampRes(String id)
    {
        return new ClasspathElementResource(OAA + id, "definingObject");
    }

    private static Resource warRes(String id)
    {
        return new ClasspathElementResource(JAVAX_MAIL + id, "definingObject");
    }

    private static Conflict conflict(ClasspathElementResource resource, Set<String> classes)
    {
        return new JakartaMigrationConflict(resource, classes, "7.4.1");
    }
}
