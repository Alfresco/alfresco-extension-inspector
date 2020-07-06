/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.service;

import static org.alfresco.ampalyser.analyser.result.Conflict.Type.BEAN_OVERWRITE;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.RESTRICTED_BEAN_CLASS;
import static org.alfresco.ampalyser.analyser.service.AnalyserService.groupByTypeAndResourceId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.alfresco.ampalyser.analyser.result.AbstractConflict;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.junit.jupiter.api.Test;

public class AnalyserServiceTest
{
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGroupByTypeAndResourceId() throws IOException
    {
        File testData = new File(
            getClass().getClassLoader().getResource("conflictsPerWarVersion.json").getFile());
        Map<String, List<Conflict>> conflictsPerWarVersion = objectMapper
            .readValue(testData, new TypeReference<>()
            {
            });

        File expectedResultFile = new File(
            getClass().getClassLoader().getResource("conflictsPerTypeAndResourceId.json")
                .getFile());
        Map<Conflict.Type, Map<String, Set<AbstractConflict>>> expectedResult = objectMapper
            .readValue(expectedResultFile, new TypeReference<>()
            {
            });
        assertNotNull(expectedResult);

        // convert map and verify result
        Map<Conflict.Type, Map<String, Set<Conflict>>> result = groupByTypeAndResourceId(
            conflictsPerWarVersion);

        assertNotNull(result);
        assertEquals(expectedResult, result);
        assertEquals(2, result.size());
        assertEquals(3, result.get(BEAN_OVERWRITE).size());
        assertEquals(1, result.get(RESTRICTED_BEAN_CLASS).size());
    }
}
