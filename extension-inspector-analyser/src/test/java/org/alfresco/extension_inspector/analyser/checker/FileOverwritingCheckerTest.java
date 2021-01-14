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
import static org.alfresco.extension_inspector.model.Resource.Type.FILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.service.ConfigService;
import org.alfresco.extension_inspector.analyser.service.ExtensionResourceInfoService;
import org.alfresco.extension_inspector.model.FileResource;
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
public class FileOverwritingCheckerTest
{
    @Mock
    private ConfigService configService;
    @InjectMocks
    private ExtensionResourceInfoService extensionResourceInfoService = spy(ExtensionResourceInfoService.class);
    @InjectMocks
    private Checker foChecker = new FileOverwritingChecker();

    @Test
    public void happyFlowTest()
    {
        // This resource shoud generate a conflict
        FileResource ampFR1 = new FileResource("/web/fr1.txt", "/web/fr1.txt");
        // This resource should not generate a conflict because to the mapping
        FileResource ampFR2 = new FileResource("/web/abc/fr2.txt", "/web/abc/fr2.txt");
        doReturn(Set.of(ampFR1, ampFR2)).when(configService).getExtensionResources(FILE);

        InventoryReport warReport = new InventoryReport();
        warReport.setAlfrescoVersion("6.66");
        Map<Resource.Type, Set<Resource>> warResources = new HashMap<>();
        FileResource warFR1 = new FileResource("/fr1.txt", "/fr1.txt");
        FileResource warFR2 = new FileResource("/abc/fr2.txt", "/abc/fr2.txt");
        warResources.put(FILE, Set.of(warFR1, warFR2));

        warReport.addResources(warResources);

        final Map<String, String> fileMappings = Map.of(
            "/web", "/",
            "/web/abc", "/def",
            "include.default", "true"
        );

        doReturn("something.amp").when(configService).getExtensionPath();
        doReturn(fileMappings).when(configService).getFileMappings();

        List<Conflict> conflicts = foChecker.process(warReport, "6.66").collect(toList());
        assertEquals(1, conflicts.size());

        Conflict conflict = conflicts.get(0);
        assertEquals(ampFR1.getId(), conflict.getAmpResourceInConflict().getId());
        assertEquals(warFR1.getId(), conflict.getWarResourceInConflict().getId());
        assertEquals("6.66", conflict.getAlfrescoVersion());
    }
}
