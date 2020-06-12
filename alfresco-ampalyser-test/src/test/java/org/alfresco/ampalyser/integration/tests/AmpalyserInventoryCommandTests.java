/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.integration.tests;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.alfresco.ampalyser.util.TestResource;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AmpalyserInventoryCommandTests extends AmpalyserInventoryTests
{
    @Test
    public void runCommandWithOutput()
    {
        String warResourcePath = TestResource.getTestResourcePath("inventoryTest.war");
        String inventoryReportPath = TestResource.getTargetPath();
        List<String> cmdOptions = List.of(warResourcePath, "--o=" + inventoryReportPath);

        // Generate new inventory report
        cmdOut = client.runInventoryAnalyserCommand(cmdOptions);
        File inventoryReport = new File(inventoryReportPath + File.separator + TestResource.getTestInventoryReport().getName());

        Assert.assertEquals(cmdOut.getExitCode(), 0);
        Assert.assertTrue(cmdOut.containsMessage(SUCCESS_MESSAGE), "Inventory report has not been generated");
        Assert.assertTrue(inventoryReport.exists());
    }

    @Test
    public void runCommandWithExitCodeError()
    {
        cmdOut = client.runInventoryAnalyserCommand(Collections.emptyList());
        Assert.assertEquals(cmdOut.getExitCode(), 1);
        Assert.assertTrue(cmdOut.containsMessage("Missing war file."));

        cmdOut = client.runInventoryAnalyserCommand(List.of("nonExisting.war"));
        Assert.assertEquals(cmdOut.getExitCode(), 1);
        Assert.assertTrue(cmdOut.containsMessage("The war file is not valid"));
    }
}
