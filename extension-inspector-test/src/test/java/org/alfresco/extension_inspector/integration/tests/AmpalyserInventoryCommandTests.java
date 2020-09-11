/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.extension_inspector.integration.tests;

import static java.io.File.separator;
import static org.alfresco.extension_inspector.util.TestResource.SUCCESS_MESSAGE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.alfresco.extension_inspector.ExtensionInspectorClient;
import org.alfresco.extension_inspector.models.CommandOutput;
import org.alfresco.extension_inspector.util.AppConfig;
import org.alfresco.extension_inspector.util.TestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(classes = AppConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AmpalyserInventoryCommandTests extends AbstractTestNGSpringContextTests
{
    @Autowired
    private ExtensionInspectorClient client;

    private CommandOutput cmdOut;

    @Test
    public void runCommandWithOutput()
    {
        String warResourcePath = TestResource.getTestResourcePath("inventoryTest.war");
        String inventoryReportPath = TestResource.getTargetPath();
        List<String> cmdOptions = List.of(warResourcePath, "--o=" + inventoryReportPath);

        // Generate new inventory report
        cmdOut = client.runAmpalyserInventoryCommand(cmdOptions);
        final File inventoryReport = new File(
            inventoryReportPath + separator + TestResource.getTestInventoryReport().getName());

        assertEquals(cmdOut.getExitCode(), 0);
        assertTrue(cmdOut.isInOutput(SUCCESS_MESSAGE), "Inventory report has not been generated");
        assertTrue(inventoryReport.exists());
    }

    @Test
    public void runCommandWithExitCodeError()
    {
        cmdOut = client.runAmpalyserInventoryCommand(Collections.emptyList());
        assertEquals(cmdOut.getExitCode(), 1);
        assertTrue(cmdOut.isInOutput("Missing war file."));

        cmdOut = client.runAmpalyserInventoryCommand(List.of("nonExisting.war"));
        assertEquals(cmdOut.getExitCode(), 1);
        assertTrue(cmdOut.isInOutput("The war file is not valid"));
    }
}
