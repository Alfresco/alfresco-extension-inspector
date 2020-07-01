/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alfresco.ampalyser.analyser.result.Conflict;

public interface ConflictPrinter
{
    void print(Map<String, Set<Conflict>> conflicts, boolean verbose);
    
    Conflict.Type getConflictType();
    
    static String joinWarVersions(Set<Conflict> conflictSet)
    {
        return conflictSet
            .stream()
            .map(Conflict::getAlfrescoVersion)
            .sorted()
            .collect(Collectors.joining(", "));
    }

    static String joinExtensionDefiningObjs(Set<Conflict> conflictSet)
    {
        return conflictSet
            .stream()
            .map(conflict -> conflict.getAmpResourceInConflict().getDefiningObject())
            .distinct()
            .sorted()
            .collect(Collectors.joining(", "));
    }
}
