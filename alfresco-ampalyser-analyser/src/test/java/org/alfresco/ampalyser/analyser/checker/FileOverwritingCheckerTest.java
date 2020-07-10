/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.stream.Collectors.toList;
import static org.alfresco.ampalyser.model.Resource.Type.FILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.service.ConfigService;
import org.alfresco.ampalyser.model.FileResource;
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
public class FileOverwritingCheckerTest
{
    @Mock
    private ConfigService configService;
    @InjectMocks
    private Checker foChecker = new FileOverwritingChecker();

    @Test
    public void happyFlowTest()
    {
        // This resource shoud generate a conflict
        FileResource ampFR1 = new FileResource("/web/fr1.txt", "/web/fr1.txt");
        // This resource should not generate a conflict because to the mapping
        FileResource ampFR2 = new FileResource("/web/abc/fr2.txt", "/web/abc/fr2.txt");
        doReturn(List.of(ampFR1, ampFR2)).when(configService).getExtensionResources(FILE);

        InventoryReport warReport = new InventoryReport();
        warReport.setAlfrescoVersion("6.66");
        Map<Resource.Type, List<Resource>> warResources = new HashMap<>();
        FileResource warFR1 = new FileResource("/fr1.txt", "/fr1.txt");
        FileResource warFR2 = new FileResource("/abc/fr2.txt", "/abc/fr2.txt");
        warResources.put(FILE, List.of(warFR1, warFR2));

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
