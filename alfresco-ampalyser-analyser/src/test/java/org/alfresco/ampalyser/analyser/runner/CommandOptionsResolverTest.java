/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.runner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.alfresco.ampalyser.analyser.store.AlfrescoTargetVersionParser;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

@ExtendWith(MockitoExtension.class)
public class CommandOptionsResolverTest
{
    @Mock
    private AlfrescoTargetVersionParser alfrescoTargetVersionParser;
    @InjectMocks
    private CommandOptionsResolver commandOptionsResolver;

    @Test
    public void testExtractVersion()
    {
        assertThrows(IllegalArgumentException.class, () -> commandOptionsResolver
            .extractTargetVersions(
                new DefaultApplicationArguments("extensionFileName", "--target-version=5.0.a")));
    }
}
