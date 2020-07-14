/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.service;

import static java.util.Objects.requireNonNull;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.BEAN_OVERWRITE;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.RESTRICTED_BEAN_CLASS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.alfresco.ampalyser.analyser.result.AbstractConflict;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.alfresco.ampalyser.model.InventoryReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class AnalyserServiceTest
{
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Mock
    private ConfigService configService;
    @Mock
    private WarInventoryReportStore warInventoryStore;
    @Mock
    private WarComparatorService warComparatorService;
    @Mock
    private AnalyserOutputService outputService;
    @InjectMocks
    private AnalyserService analyserService;

    @Test
    public void testGroupByTypeAndResourceId() throws IOException
    {
        final Map<String, List<Conflict>> conflictsPerWarVersion = OBJECT_MAPPER.readValue(
            requireNonNull(getClass().getClassLoader().getResourceAsStream("conflictsPerWarVersion.json")),
            new TypeReference<>() {});

        final Map<Conflict.Type, Map<String, Set<AbstractConflict>>> expectedResult = OBJECT_MAPPER.readValue(
            requireNonNull(getClass().getClassLoader().getResourceAsStream("conflictsPerTypeAndResourceId.json")),
            new TypeReference<>() {});
        assertNotNull(expectedResult);

        doReturn(new InventoryReport()).when(warInventoryStore).retrieve(any());

        when(warComparatorService.findConflicts(any(), any()))
            .thenReturn(conflictsPerWarVersion.get("6.0.1").stream())
            .thenReturn(conflictsPerWarVersion.get("6.0.0.3").stream())
            .thenReturn(conflictsPerWarVersion.get("6.0.0.5").stream());

        // call the service method and capture its internal variables
        analyserService.analyseAgainstKnownVersions(new TreeSet<>(Set.of("6.0.1", "6.0.0.3", "6.0.0.5")));

        final ArgumentCaptor<Map<Conflict.Type, Map<String, Set<Conflict>>>> captor =
            ArgumentCaptor.forClass(Map.class);
        verify(outputService).print(captor.capture());

        Map<Conflict.Type, Map<String, Set<Conflict>>> result = captor.getValue();
        assertNotNull(result);
        assertEquals(expectedResult, result);
        assertEquals(2, result.size());
        assertEquals(3, result.get(BEAN_OVERWRITE).size());
        assertEquals(1, result.get(RESTRICTED_BEAN_CLASS).size());
    }
}
