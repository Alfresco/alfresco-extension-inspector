/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.alfresco.extension_inspector.analyser.checker;

import static java.util.stream.Collectors.toList;
import static org.alfresco.extension_inspector.model.Resource.Type.BEAN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.service.ConfigService;
import org.alfresco.extension_inspector.analyser.service.ExtensionResourceInfoService;
import org.alfresco.extension_inspector.model.BeanResource;
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
public class BeanOverwritingCheckerTest
{
    @Mock
    private ConfigService configService;
    @InjectMocks
    private ExtensionResourceInfoService extensionResourceInfoService = spy(ExtensionResourceInfoService.class);
    @InjectMocks
    private BeanOverwritingChecker boChecker;

    @Test
    public void happyFlowTest()
    {
        // This one should be allowed by the allowedList
        BeanResource ampBR1 = new BeanResource("bean1", "context.xml", "org.alfresco.Dummy");
        // This one should generate multiple conflicts
        BeanResource ampBR2 = new BeanResource("bean2", "amp_context.xml", "org.alfresco.Dummy");
        // This one should be ok as it doesn't overwrite anything
        BeanResource ampBR3 = new BeanResource("bean3", "amp_context.xml", "org.alfresco.Dummy");
        when(configService.getExtensionResources(eq(BEAN))).thenReturn(Set.of(ampBR1, ampBR2, ampBR3));

        InventoryReport warReport = new InventoryReport();
        warReport.setAlfrescoVersion("6.66");
        Map<Resource.Type, Set<Resource>> warResources = new HashMap<>();
        BeanResource warBR1 = new BeanResource("bean1", "context.xml", "org.alfresco.Dummy");
        BeanResource warBR21 = new BeanResource("bean2", "war_context1.xml", "org.alfresco.Dummy");
        BeanResource warBR22 = new BeanResource("bean2", "war_context2.xml", "org.alfresco.Dummy");
        BeanResource warBR3 = new BeanResource("bean4", "war_main_context.xml", "org.alfresco.Dummy");
        warResources.put(BEAN, new LinkedHashSet<>(List.of(warBR1, warBR21, warBR22, warBR3)));

        warReport.addResources(warResources);

        when(configService.getBeanOverrideAllowedList()).thenReturn(Set.of("bean1"));

        List<Conflict> conflicts = boChecker.process(warReport, "6.66").collect(toList());
        assertEquals(2, conflicts.size());

        Conflict conflict1 = conflicts.get(0);
        assertEquals(ampBR2, conflict1.getAmpResourceInConflict());
        assertEquals(warBR21, conflict1.getWarResourceInConflict());
        assertEquals("6.66", conflict1.getAlfrescoVersion());

        Conflict conflict2 = conflicts.get(1);
        assertEquals(ampBR2, conflict2.getAmpResourceInConflict());
        assertEquals(warBR22, conflict2.getWarResourceInConflict());
        assertEquals("6.66", conflict2.getAlfrescoVersion());
    }
}
