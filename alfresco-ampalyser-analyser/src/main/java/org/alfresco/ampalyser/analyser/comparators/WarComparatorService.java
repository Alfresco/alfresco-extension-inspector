package org.alfresco.ampalyser.analyser.comparators;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.Collection;
import java.util.List;

import org.alfresco.ampalyser.analyser.comparators.checkers.Checker;
import org.alfresco.ampalyser.analyser.model.Conflict;
import org.alfresco.ampalyser.model.InventoryReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WarComparatorService
{
    @Autowired
    private List<Checker> checkers;

    public List<Conflict> findConflicts(final InventoryReport ampInventory, final InventoryReport warInventory)
    {
        // call all checkers and return result
        return checkers
            .stream()
            .map(c -> c.analyse(
                ampInventory.getResources().get(c.resourceType()),
                warInventory.getResources().get(c.resourceType())
            ))
            .flatMap(Collection::stream)
            .collect(toUnmodifiableList());
    }
}
