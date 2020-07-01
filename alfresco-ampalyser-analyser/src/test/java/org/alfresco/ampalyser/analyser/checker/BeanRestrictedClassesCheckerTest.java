package org.alfresco.ampalyser.analyser.checker;

import static org.alfresco.ampalyser.analyser.checker.BeanRestrictedClassesChecker.WHITELIST_BEAN_RESTRICTED_CLASSES;
import static org.alfresco.ampalyser.analyser.checker.Checker.ALFRESCO_VERSION;
import static org.alfresco.ampalyser.analyser.service.AnalyserService.EXTENSION_FILE_TYPE;
import static org.alfresco.ampalyser.model.Resource.Type.ALFRESCO_PUBLIC_API;
import static org.alfresco.ampalyser.model.Resource.Type.BEAN;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.model.AlfrescoPublicApiResource;
import org.alfresco.ampalyser.model.BeanResource;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.junit.jupiter.api.Test;

/**
 * @author Lucian Tuca
 */
public class BeanRestrictedClassesCheckerTest
{
    private BeanRestrictedClassesChecker brcChecker = new BeanRestrictedClassesChecker();

    @Test
    public void happyFlowTest()
    {
        InventoryReport ampReport = new InventoryReport();
        Map<Resource.Type, List<Resource>> ampResources = new HashMap<>();

        // This one should be allowed by the ALFRESCO_PUBLIC_API resources in the WAR
        BeanResource ampBR1 = new BeanResource("bean1", "context.xml", "org.alfresco.C1");
        // This one should be allowed by the user added list
        BeanResource ampBR2 = new BeanResource("bean2", "amp_context.xml", "org.alfresco.C2");
        // This one should generate a conflict
        BeanResource ampBR3 = new BeanResource("bean3", "amp_context.xml", "org.alfresco.C3");
        ampResources.put(BEAN, List.of(ampBR1, ampBR2, ampBR3));

        ampReport.addResources(ampResources);

        InventoryReport warReport = new InventoryReport();
        warReport.setAlfrescoVersion("6.66");
        Map<Resource.Type, List<Resource>> warResources = new HashMap<>();
        AlfrescoPublicApiResource warAPAR1 = new AlfrescoPublicApiResource("org.alfresco.C1", false);
        AlfrescoPublicApiResource warAPAR2 = new AlfrescoPublicApiResource("org.alfresco.D1", false);
        warResources.put(ALFRESCO_PUBLIC_API, List.of(warAPAR1, warAPAR2));

        warReport.addResources(warResources);

        Map<String, Object> extraInfo = Map.of(
            WHITELIST_BEAN_RESTRICTED_CLASSES, Set.of("org.alfresco.C2"),
            EXTENSION_FILE_TYPE, "amp",
            ALFRESCO_VERSION, "6.66");

        List<Conflict> conflicts = brcChecker.process(ampReport, warReport, extraInfo);
        assertEquals(1, conflicts.size());

        Conflict conflict1 = conflicts.get(0);
        assertEquals(ampBR3, conflict1.getAmpResourceInConflict());
        assertEquals("6.66", conflict1.getAlfrescoVersion());
    }
}
