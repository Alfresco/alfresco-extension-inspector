/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.analyser.printers;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static org.alfresco.ampalyser.analyser.printers.ConflictPrinter.joinWarVersions;
import static org.alfresco.ampalyser.analyser.result.Conflict.Type.CUSTOM_CODE;

import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.CustomCodeConflict;
import org.springframework.stereotype.Component;

@Component
public class CustomCodeConflictPrinter implements ConflictPrinter
{
    private static final String HEADER =
        "Found usage of internal Alfresco classes! Alfresco provides a Java API "
            + "that is clearly marked as @AlfrescoPublicAPI. Any other classes or interfaces in "
            + "the Alfresco repository are considered our internal implementation detail and might "
            + "change or even disappear in service packs and new versions without prior notice. "
            + "The following classes are making use of internal Alfresco classes:";
    
    @Override
    public String getHeader()
    {
        return HEADER;
    }

    @Override
    public Conflict.Type getConflictType()
    {
        return CUSTOM_CODE;
    }

    @Override
    public void printVerboseOutput(final String id, final Set<Conflict> conflictSet)
    {
        final String definingObject = conflictSet.iterator().next().getAmpResourceInConflict().getDefiningObject();
        final Map<String, String> dependenciesPerAlfrescoVersion = conflictSet
            .stream()
            .map(c -> (CustomCodeConflict) c)
            .collect(groupingBy(
                Conflict::getAlfrescoVersion,
                flatMapping(c -> c.getInvalidAlfrescoDependencies().stream().sorted(), joining(", "))
            ));

        System.out.println(format("Extension resource <{0}@{1}> has invalid (non PublicAPI) dependencies:",
            id, definingObject));

        dependenciesPerAlfrescoVersion
            .forEach((k, v) -> System.out.println(k + ": " + v));

        System.out.println();
    }

    @Override
    public void print(final String id, final Set<Conflict> conflictSet)
    {
        final String definingObject = conflictSet.iterator().next().getAmpResourceInConflict().getDefiningObject();

        System.out.println(format(
            "Extension resource <{0}@{1}> has invalid (non PublicAPI) dependencies in: {2}",
            id, definingObject, joinWarVersions(conflictSet)));

        System.out.println();
    }
}
