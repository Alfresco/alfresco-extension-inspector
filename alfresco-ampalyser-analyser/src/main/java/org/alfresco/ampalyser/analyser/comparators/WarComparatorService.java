package org.alfresco.ampalyser.analyser.comparators;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import org.alfresco.ampalyser.analyser.checker.Checker;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WarComparatorService
{
    @Autowired
    private List<Checker> checkers;

    public Map<Resource.Type, List<Conflict>> findConflicts(final InventoryReport ampInventory,
        final InventoryReport warInventory, final Map<String, Object> extraInfo)
    {
        // Call all checkers and return the found lists of conflicts mapped by resource types
        return checkers
            .stream()
            .collect(toMap(Checker::resourceType, c -> c.process(
                ampInventory.getResources().get(c.resourceType()),
                warInventory.getResources().get(c.resourceType()),
                extraInfo)
            ));
    }
}
