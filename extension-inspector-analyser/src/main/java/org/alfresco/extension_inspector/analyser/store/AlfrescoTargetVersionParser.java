/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.extension_inspector.analyser.store;

import static java.util.Collections.emptySortedSet;
import static java.util.Collections.unmodifiableSortedSet;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlfrescoTargetVersionParser
{
    private static final Logger logger = LoggerFactory.getLogger(AlfrescoTargetVersionParser.class);

    @Autowired
    private WarInventoryReportStore inventoryStore;

    public SortedSet<String> parse(final Collection<String> targetStrings)
    {
        // if target option was not provided, compare with all the known WARs
        if (targetStrings == null)
        {
            return unmodifiableSortedSet(inventoryStore.allKnownVersions());
        }

        // if target option was provided by without a value, throw exception
        if (targetStrings.isEmpty())
        {
            logger.error("Invalid target options (missing values)!");
            return emptySortedSet();
        }

        // if target option was provided at least once, parse the strings (in the provided order)
        final List<String> targetRanges = targetStrings
            .stream()
            .flatMap(AlfrescoTargetVersionParser::parseTargetString)
            .collect(toUnmodifiableList());

        if (targetRanges.isEmpty())
        {
            logger.error("Invalid target options (invalid values)!");
            return emptySortedSet();
        }

        final SortedSet<String> versions = targetRanges
            .stream()
            .flatMap(tr -> inventoryStore.knownVersions(tr).stream())
            .collect(toCollection(() -> new TreeSet<>(comparing(ComparableVersion::new))));

        return unmodifiableSortedSet(versions);
    }

    private static Stream<String> parseTargetString(final String targetString)
    {
        return Arrays
            .stream(targetString.split(",")) // results in [ "6.0", "5.1-5.2", " ", "", " 6.1-6.2.1" ]
            .map(String::trim)
            .filter(s -> !isBlank(s));
    }
}
