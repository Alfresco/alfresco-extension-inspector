package org.alfresco.ampalyser.integration.tests;

import org.alfresco.ampalyser.AmpalyserClient;
import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.util.TestResource;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;

public class AmpalyserInventoryTests
{
        @Test
        public void runAmpalyserOnWar()
        {
                String warResourcePath = TestResource.getTestResourcePath("inventoryTest.war");
                List<String> cmdOptions = new ArrayList<>(){{add(warResourcePath);}};

                AmpalyserClient client = new AmpalyserClient();
                CommandOutput cmdOut = client.runInventoryAnalyserCommand(cmdOptions);
                Assert.assertEquals(cmdOut.getExitCode(), 0);
        }
}
