/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.Collections.unmodifiableList;
import static org.alfresco.ampalyser.analyser.checker.Checker.ALFRESCO_VERSION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.alfresco.ampalyser.analyser.result.ClasspathConflict;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.model.ClasspathElementResource;
import org.alfresco.ampalyser.model.Resource;
import org.junit.jupiter.api.Test;

class ClasspathConflictsCheckerTest
{
    private ClasspathConflictsChecker checker = new ClasspathConflictsChecker();

    @Test
    void testProcessInternal()
    {
        final Collection<Resource> ampResources = List.of(
            new ClasspathElementResource("/org/alfresco/Black.class", "/WEB-INF/lib/lib-west-2.0.jar"),
            new ClasspathElementResource("/org/alfresco/Blue.class", "/WEB-INF/lib/lib-east-1.0.jar"),
            new ClasspathElementResource("/org/alfresco/White.class", "/WEB-INF/lib/lib-south-1.0.jar"),
            new ClasspathElementResource("/org/alfresco/Purple.class", "/WEB-INF/lib/lib-south-1.0.jar")
        );
        final Collection<Resource> warResources = List.of(
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

        final List<Conflict> actualResult = checker.processInternal(ampResources, warResources,
            Map.of(ALFRESCO_VERSION, "6.0.0"));
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.containsAll(expectedResult));
    }
}