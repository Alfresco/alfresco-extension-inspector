package org.alfresco.ampalyser.analyser.checker;

import static org.alfresco.ampalyser.analyser.checker.BeanOverwritingChecker.BEAN_OVERRIDING_WHITELIST;
import static org.alfresco.ampalyser.model.Resource.Type.BEAN;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.model.BeanResource;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.junit.Test;

/**
 * @author Lucian Tuca
 */
public class BeanOverwritingCheckerTest
{
    private BeanOverwritingChecker boChecker = new BeanOverwritingChecker();

    @Test
    public void happyFlowTest()
    {
        InventoryReport ampReport = new InventoryReport();
        Map<Resource.Type, List<Resource>> ampResources = new HashMap<>();

        // This one should be allowed by the whitelist
        BeanResource ampBR1 = new BeanResource("bean1", "context.xml", "org.alfresco.Dummy");
        // This one should generate multiple conflicts
        BeanResource ampBR2 = new BeanResource("bean2", "amp_context.xml", "org.alfresco.Dummy");
        // This one should be ok as it doesn't overwrite anything
        BeanResource ampBR3 = new BeanResource("bean3", "amp_context.xml", "org.alfresco.Dummy");
        ampResources.put(BEAN, List.of(ampBR1, ampBR2, ampBR3));

        ampReport.addResources(ampResources);

        InventoryReport warReport = new InventoryReport();
        warReport.setAlfrescoVersion("6.66");
        Map<Resource.Type, List<Resource>> warResources = new HashMap<>();
        BeanResource warBR1 = new BeanResource("bean1", "context.xml", "org.alfresco.Dummy");
        BeanResource warBR21 = new BeanResource("bean2", "war_context1.xml", "org.alfresco.Dummy");
        BeanResource warBR22 = new BeanResource("bean2", "war_context2.xml", "org.alfresco.Dummy");
        BeanResource warBR3 = new BeanResource("bean4", "war_main_context.xml", "org.alfresco.Dummy");
        warResources.put(BEAN, List.of(warBR1, warBR21, warBR22, warBR3));

        warReport.addResources(warResources);

        Map<String, Object> extraInfo = Map.of(
            BEAN_OVERRIDING_WHITELIST, Set.of("bean1")
            );

        List<Conflict> conflicts = boChecker.process(ampReport.getResources().get(BEAN), warReport.getResources().get(BEAN), extraInfo);
        assertEquals(2, conflicts.size());

        Conflict conflict1 = conflicts.get(0);
        assertEquals(ampBR2, conflict1.getAmpResourceInConflict());
        assertEquals(warBR21, conflict1.getWarResourceInConflict());

        Conflict conflict2 = conflicts.get(1);
        assertEquals(ampBR2, conflict2.getAmpResourceInConflict());
        assertEquals(warBR22, conflict2.getWarResourceInConflict());
    }
}
