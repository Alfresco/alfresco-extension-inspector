package org.alfresco.ampalyser.analyser.comparators.checkers;

import java.util.Collection;
import java.util.List;

import org.alfresco.ampalyser.analyser.model.Conflict;
import org.alfresco.ampalyser.model.Resource;

public interface Checker
{
    List<Conflict> analyse(Collection<Resource> ampResources, Collection<Resource> warResources);

    Resource.Type resourceType();

    // TODO: add actual Checker implementations (as beans) in ACS-76, ACS-77, etc.
}
