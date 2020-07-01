/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static org.alfresco.ampalyser.analyser.printers.ConflictPrinter.joinWarVersions;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.RESTRICTED_BEAN_CLASS;

import java.util.Set;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.springframework.stereotype.Component;

@Component
public class RestrictedBeanConflictPrinter implements ConflictPrinter
{
    private static final String HEADER = "Found beans for restricted classes! The following beans "
        + "instantiate classes from Alfresco or 3rd party libraries which are "
        + "not meant to be instantiated by custom beans:";

    @Override
    public String getHeader()
    {
        return HEADER;
    }

    @Override
    public Conflict.Type getConflictType()
    {
        return RESTRICTED_BEAN_CLASS;
    }

    @Override
    public void printVerboseOutput(String id, Set<Conflict> conflictSet)
    {
        System.out.println(id);
        System.out.println("Instantiating restricted class from " + joinWarVersions(conflictSet));
        System.out.println();
    }

    @Override
    public void print(String id, Set<Conflict> conflictSet)
    {
        System.out.println(id);
    }
}
