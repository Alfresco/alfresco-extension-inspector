package org.alfresco.ampalyser.analyser.comparators.checkers;

import java.util.Collection;
import java.util.List;

import org.alfresco.ampalyser.analyser.model.Conflict;
import org.alfresco.ampalyser.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class FileOverwriteChecker implements Checker
{
    @Override
    public List<Conflict> analyse(Collection<Resource> ampResources, Collection<Resource> warResources)
    {
        //TODO ACS-76
        return null;
    }

    @Override
    public Resource.Type resourceType()
    {
        return Resource.Type.FILE;
    }
}
