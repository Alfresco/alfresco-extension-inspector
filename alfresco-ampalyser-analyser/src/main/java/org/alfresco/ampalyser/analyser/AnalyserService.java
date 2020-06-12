package org.alfresco.ampalyser.analyser;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.comparators.WarComparatorService;
import org.alfresco.ampalyser.analyser.model.Conflict;
import org.alfresco.ampalyser.analyser.store.WarInventoryReportStore;
import org.alfresco.ampalyser.inventory.service.InventoryService;
import org.alfresco.ampalyser.model.InventoryReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyserService
{
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private WarInventoryReportStore warInventoryStore;

    @Autowired
    private WarComparatorService warComparatorService;

    public void analyse(final String ampPath, final String alfrescoVersionRange)
    {
        // build the *ampInventoryReport*:
        final InventoryReport ampInventory = inventoryService.extractInventoryReport(ampPath);

        // compare with all the requested wars and build a collection with the results:
        final Set<String> versions = warInventoryStore.knownVersions(alfrescoVersionRange);

        final Map<String, List<Conflict>> conflictsPerWarVersion = versions
            .stream()
            .collect(toMap(identity(), v -> warComparatorService.findConflicts(
                ampInventory,
                warInventoryStore.retrieve(v))
            ));

        //TODO ACS-192 Process results and generate output, e.g.
        // > /foo/bar.jar - conflicting with 4.2.0, 4.2.1, 4.2.3, 4.2.4, 4.2.5
    }
}
