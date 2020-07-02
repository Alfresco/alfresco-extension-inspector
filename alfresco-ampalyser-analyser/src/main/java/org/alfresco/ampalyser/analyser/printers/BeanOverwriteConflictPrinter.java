/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.printers.ConflictPrinter.joinWarVersions;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.BEAN_OVERWRITE;

import java.util.Set;
import java.util.stream.Collectors;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.springframework.stereotype.Component;

@Component
public class BeanOverwriteConflictPrinter implements ConflictPrinter
{
    private static final String HEADER =
        "Found bean overwrites! Spring beans defined by Alfresco constitute "
            + "a fundamental building block of the repository and must not be "
            + "overwritten unless explicitly allowed. Found the following beans "
            + "overwriting default Alfresco functionality:";

    @Override
    public String getHeader()
    {
        return HEADER;
    }
    
    @Override
    public Conflict.Type getConflictType()
    {
        return BEAN_OVERWRITE;
    }

    @Override
    public void printVerboseOutput(String id, Set<Conflict> conflictSet)
    {
        System.out.println(id + " defined in " + joinExtensionDefiningObjs(conflictSet));
        System.out.println("Overwriting bean in " + joinWarVersions(conflictSet));
        System.out.println();
    }

    @Override
    public void print(String id, Set<Conflict> conflictSet)
    {
        System.out.println(id + " defined in " + joinExtensionDefiningObjs(conflictSet));
        System.out.println();
    }

    private static String joinExtensionDefiningObjs(Set<Conflict> conflictSet)
    {
        return conflictSet
            .stream()
            .map(conflict -> conflict.getAmpResourceInConflict().getDefiningObject())
            .distinct()
            .sorted()
            .collect(Collectors.joining(", "));
    }
}
