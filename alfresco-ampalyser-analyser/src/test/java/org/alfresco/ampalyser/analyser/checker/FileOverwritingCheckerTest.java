package org.alfresco.ampalyser.analyser.checker;

import static org.alfresco.ampalyser.analyser.checker.FileOverwritingChecker.FILE_MAPPING_NAME;
import static org.alfresco.ampalyser.model.Resource.Type.FILE;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.alfresco.ampalyser.analyser.result.Result;
import org.alfresco.ampalyser.model.FileResource;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.junit.Test;

/**
 * @author Lucian Tuca
 */
public class FileOverwritingCheckerTest
{
    private Checker foChecker = new FileOverwritingChecker();

    @Test
    public void happyFlowTest()
    {
        InventoryReport ampReport = new InventoryReport();
        Map<Resource.Type, List<Resource>> ampResources = new HashMap<>();
        // This resource shoud generate a conflict
        FileResource ampFR1 = new FileResource("/web/fr1.txt", "/web/fr1.txt");
        // This resource should not generate a conflict because to the mapping
        FileResource ampFR2 = new FileResource("/web/abc/fr2.txt", "/web/abc/fr2.txt");
        ampResources.put(FILE, List.of(ampFR1, ampFR2));

        ampReport.addResources(ampResources);

        InventoryReport warReport = new InventoryReport();
        Map<Resource.Type, List<Resource>> warResources = new HashMap<>();
        FileResource warFR1 = new FileResource("/fr1.txt", "/fr1.txt");
        FileResource warFR2 = new FileResource("/abc/fr2.txt", "/abc/fr2.txt");
        warResources.put(FILE, List.of(warFR1, warFR2));

        warReport.addResources(warResources);

        Properties properties = new Properties();
        properties.putIfAbsent("/web", "/");
        properties.putIfAbsent("/web/abc", "/def");
        properties.putIfAbsent("include.default", "true");
        Map<String, Object> extraInfo = Map.of(FILE_MAPPING_NAME, List.of(properties));

        List<Result> results = foChecker.process(warReport, ampReport, extraInfo);
        assertEquals(1, results.size());
    }
}
