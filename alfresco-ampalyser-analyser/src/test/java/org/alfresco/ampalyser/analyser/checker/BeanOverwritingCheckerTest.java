/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.stream.Collectors.toList;
import static org.alfresco.ampalyser.model.Resource.Type.BEAN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.service.ConfigService;
import org.alfresco.ampalyser.analyser.service.ExtensionResourceInfoService;
import org.alfresco.ampalyser.model.BeanResource;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
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
        // This one should be allowed by the whitelist
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

        when(configService.getBeanOverrideWhitelist()).thenReturn(Set.of("bean1"));

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
