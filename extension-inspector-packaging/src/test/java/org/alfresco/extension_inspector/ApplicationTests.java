/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.extension_inspector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

@ExtendWith(MockitoExtension.class)
public class ApplicationTests
{
    @Mock
    private Application application;
    
    @Test
    public void testExecuteEmptyCommand()
    {
        application.run(new DefaultApplicationArguments());

        assertEquals(1, application.getExitCode());
    }

    @Test
    public void testExecuteListKnownVersionsCommand()
    {
        application.run(new DefaultApplicationArguments("--list-known-alfresco-versions"));
        //verify(warInventoryReportStore).allKnownVersions();

        try
        {
            application.run(
                new DefaultApplicationArguments("--list-known-alfresco-versions", "--some-option"));
            fail("`list-known-alfresco-versions` command should not have extra options.");
        }
        catch (IllegalArgumentException e)
        {
            //Expected
        }
    }

    @Test
    public void testExecuteHelpCommand()
    {
        application.run(new DefaultApplicationArguments("--help"));

        try
        {
            application.run(new DefaultApplicationArguments("help"));
        }
        catch (IllegalArgumentException e)
        {
            //Expected
        }

        try
        {
            application.run(
                new DefaultApplicationArguments("--help", "--some-option", "--some-other-option"));
            fail("`help` command should not have extra options.");
        }
        catch (IllegalArgumentException e)
        {
            //Expected
        }
    }

    @Test
    public void testExecuteUnknownCommand()
    {
        try
        {
            application.run(new DefaultApplicationArguments("--unknown"));
            fail("`unknown` command should have failed.");
        }
        catch (IllegalArgumentException e)
        {
            //Expected
        }
    }

}
