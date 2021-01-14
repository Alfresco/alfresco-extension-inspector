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
package org.alfresco.extension_inspector.analyser.checker;

import static java.util.stream.Collectors.toList;
import static org.alfresco.extension_inspector.model.Resource.Type.ALFRESCO_PUBLIC_API;
import static org.alfresco.extension_inspector.model.Resource.Type.BEAN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.service.ConfigService;
import org.alfresco.extension_inspector.analyser.service.ExtensionResourceInfoService;
import org.alfresco.extension_inspector.model.AlfrescoPublicApiResource;
import org.alfresco.extension_inspector.model.BeanResource;
import org.alfresco.extension_inspector.model.ClasspathElementResource;
import org.alfresco.extension_inspector.model.InventoryReport;
import org.alfresco.extension_inspector.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author Lucian Tuca
 */
@ExtendWith(MockitoExtension.class)
public class BeanRestrictedClassesCheckerTest
{
    @Mock
    private ConfigService configService;
    @InjectMocks
    private ExtensionResourceInfoService extensionResourceInfoService = spy(ExtensionResourceInfoService.class);
    @InjectMocks
    private BeanRestrictedClassesChecker brcChecker = new BeanRestrictedClassesChecker();

    @Test
    public void happyFlowTest()
    {

        // This one should be allowed by the ALFRESCO_PUBLIC_API resources in the WAR
        BeanResource ampBR1 = new BeanResource("bean1", "context.xml", "org.alfresco.C1");
        // This one should be allowed by the user added list
        BeanResource ampBR2 = new BeanResource("bean2", "amp_context.xml", "org.alfresco.C2");
        // This one should generate a conflict
        BeanResource ampBR3 = new BeanResource("bean3", "amp_context.xml", "org.alfresco.C3");
        // This one should be allowed as it instantiates an extension specific class 
        ClasspathElementResource ampCER4 = new ClasspathElementResource("/org/alfresco/C4",
            "/WEB-INF/lib/an_extension.jar");
        doReturn(Set.of(ampBR1, ampBR2, ampBR3)).when(configService).getExtensionResources(eq(BEAN));
        doReturn(Map.of("/org/alfresco/C4.class", Set.of(ampCER4)))
            .when(extensionResourceInfoService).retrieveClasspathElementsById();

        InventoryReport warReport = new InventoryReport();
        warReport.setAlfrescoVersion("6.66");
        Map<Resource.Type, Set<Resource>> warResources = new HashMap<>();
        AlfrescoPublicApiResource warAPAR1 = new AlfrescoPublicApiResource("org.alfresco.C1", false);
        AlfrescoPublicApiResource warAPAR2 = new AlfrescoPublicApiResource("org.alfresco.D1", false);
        warResources.put(ALFRESCO_PUBLIC_API, Set.of(warAPAR1, warAPAR2));

        warReport.addResources(warResources);

        doReturn(Set.of("org/alfresco/C2")).when(configService).getInternalClassAllowedList();

        List<Conflict> conflicts = brcChecker.process(warReport, "6.66").collect(toList());
        assertEquals(1, conflicts.size());

        Conflict conflict1 = conflicts.get(0);
        assertEquals(ampBR3, conflict1.getAmpResourceInConflict());
        assertEquals("6.66", conflict1.getAlfrescoVersion());
    }
}
