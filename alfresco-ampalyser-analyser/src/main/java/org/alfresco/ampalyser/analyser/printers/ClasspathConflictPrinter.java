/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.printers.ConflictPrinter.joinWarVersions;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.CLASSPATH_CONFLICT;

import java.util.Set;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.springframework.stereotype.Component;

@Component
public class ClasspathConflictPrinter implements ConflictPrinter
{
    private static final String HEADER =
        "Found classpath conflicts! Although it might be possible to install "
            + "this extension, its behaviour is undefined. The following resources in your "
            + "extension are in conflict with resources on the classpath in the Alfresco "
            + "repository:";

    @Override
    public String getHeader()
    {
        return HEADER;
    }

    @Override
    public Conflict.Type getConflictType()
    {
        return CLASSPATH_CONFLICT;
    }

    @Override
    public void printVerboseOutput(String id, Set<Conflict> conflictSet)
    {
        System.out.println(id);
        System.out.println("Conflicting with " + joinWarVersions(conflictSet));
        System.out.println();
    }

    @Override
    public void print(String id, Set<Conflict> conflictSet)
    {
        System.out.println(id);
        System.out.println();
    }
}
