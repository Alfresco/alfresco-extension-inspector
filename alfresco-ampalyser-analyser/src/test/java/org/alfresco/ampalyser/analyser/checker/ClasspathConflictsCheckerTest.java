/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.alfresco.ampalyser.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.List;
import java.util.Map;

import org.alfresco.ampalyser.analyser.result.ClasspathConflict;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.service.ConfigService;
import org.alfresco.ampalyser.analyser.service.ExtensionResourceInfoService;
import org.alfresco.ampalyser.model.ClasspathElementResource;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClasspathConflictsCheckerTest
{
    @Mock
    private ConfigService configService;
    @InjectMocks
    private ExtensionResourceInfoService extensionResourceInfoService = spy(ExtensionResourceInfoService.class);
    @InjectMocks
    private ClasspathConflictsChecker checker;

    @Test
    void testProcessInternal()
    {
        final List<Resource> ampResources = List.of(
            new ClasspathElementResource("/org/alfresco/Black.class", "/WEB-INF/lib/lib-west-2.0.jar"),
            new ClasspathElementResource("/org/alfresco/Blue.class", "/WEB-INF/lib/lib-east-1.0.jar"),
            new ClasspathElementResource("/org/alfresco/White.class", "/WEB-INF/lib/lib-south-1.0.jar"),
            new ClasspathElementResource("/org/alfresco/Purple.class", "/WEB-INF/lib/lib-south-1.0.jar")
        );
        final List<Resource> warResources = List.of(
            new ClasspathElementResource("/org/alfresco/Red.class", "/WEB-INF/lib/lib-west-1.0.jar"),
            new ClasspathElementResource("/org/alfresco/Black.class", "/WEB-INF/lib/lib-west-1.0.jar"),
            new ClasspathElementResource("/org/alfresco/Black.class", "/WEB-INF/lib/lib-east-1.0.jar"),
            new ClasspathElementResource("/org/alfresco/Blue.class", "/WEB-INF/lib/lib-east-1.0.jar"),
            new ClasspathElementResource("/org/alfresco/White.class", "/WEB-INF/lib/lib-north-1.0.jar")
        );

        final List<Conflict> expectedResult = unmodifiableList(List.of(
            new ClasspathConflict(
                new ClasspathElementResource("/org/alfresco/Black.class", "/WEB-INF/lib/lib-west-2.0.jar"),
                new ClasspathElementResource("/org/alfresco/Black.class", "/WEB-INF/lib/lib-west-1.0.jar"),
                "6.0.0"
            ),

            new ClasspathConflict(
                new ClasspathElementResource("/org/alfresco/Black.class", "/WEB-INF/lib/lib-west-2.0.jar"),
                new ClasspathElementResource("/org/alfresco/Black.class", "/WEB-INF/lib/lib-east-1.0.jar"),
                "6.0.0"
            ),

            new ClasspathConflict(
                new ClasspathElementResource("/org/alfresco/Blue.class", "/WEB-INF/lib/lib-east-1.0.jar"),
                new ClasspathElementResource("/org/alfresco/Blue.class", "/WEB-INF/lib/lib-east-1.0.jar"),
                "6.0.0"
            ),

            new ClasspathConflict(
                new ClasspathElementResource("/org/alfresco/White.class", "/WEB-INF/lib/lib-south-1.0.jar"),
                new ClasspathElementResource("/org/alfresco/White.class", "/WEB-INF/lib/lib-north-1.0.jar"),
                "6.0.0"
            )
        ));

        InventoryReport warReport = new InventoryReport();
        warReport.setResources(Map.of(CLASSPATH_ELEMENT, warResources));

        doReturn(ampResources).when(configService).getExtensionResources(eq(CLASSPATH_ELEMENT));

        final List<Conflict> actualResult = checker.processInternal(warReport, "6.0.0").collect(toList());
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.containsAll(expectedResult));
    }
}