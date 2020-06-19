package org.alfresco.ampalyser.analyser.comparators;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.alfresco.ampalyser.analyser.checker.Checker;
import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.model.InventoryReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WarComparatorService
{
    @Autowired
    private List<Checker> checkers;

    public List<Conflict> findConflicts(final InventoryReport ampInventory, final InventoryReport warInventory, final Map<String, Object> extraInfo)
    {
        // Call all checkers and return the found list of conflicts
        return checkers
            .stream()
            .sorted(comparing(Checker::resourceType))
            .map(c -> c.process(
                ampInventory,
                warInventory,
                extraInfo)
            )
            .flatMap(Collection::stream)
            .collect(toUnmodifiableList());
    }
}
