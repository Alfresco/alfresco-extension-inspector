package org.alfresco.ampalyser.analyser.store;

import static java.util.Collections.emptySet;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.alfresco.ampalyser.analyser.parser.InventoryParser;
import org.alfresco.ampalyser.model.InventoryReport;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

@Service
public class WarInventoryReportStore
{
    private static final Logger logger = LoggerFactory.getLogger(WarInventoryReportStore.class);

    @Value("${inventory-report-resource-pattern}")
    private String inventoryReportResourcePattern;

    @Autowired
    private InventoryParser inventoryParser;

    private Map<String, AbstractResource> inventoryReportResources;

    @PostConstruct
    private void init() throws Exception
    {
        final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());

        inventoryReportResources = Arrays
            .stream(resolver.getResources(inventoryReportResourcePattern))
            .filter(r -> r instanceof AbstractResource)
            .collect(toMap(
                r -> requireNonNull(r.getFilename()).replace(".json", ""),
                r -> (AbstractResource) r
            ));
    }

    public InventoryReport retrieve(final String alfrescoVersion)
    {
        if (!isKnown(alfrescoVersion))
        {
            throw new RuntimeException("No WAR inventory found for Alfresco Version: " + alfrescoVersion);
        }

        try (final InputStream is = inventoryReportResources.get(alfrescoVersion).getInputStream())
        {
            return inventoryParser.parseReport(is);
        }
        catch (IOException e)
        {
            logger.error("Failed to read inventory resource for version: " + alfrescoVersion, e);
            throw new RuntimeException("Failed to read inventory resource for version: " + alfrescoVersion, e);
        }
    }

    public Set<String> allKnownVersions()
    {
        return inventoryReportResources
            .keySet()
            .stream()
            .collect(toCollection(() -> new TreeSet<>(comparing(ComparableVersion::new))));
    }

    public Set<String> knownVersions(final String alfrescoVersionRange)
    {
        final String[] versionStrings = alfrescoVersionRange.split("-");
        if (versionStrings.length == 1)
        {
            final String version = versionStrings[0];
            if (!isKnown(version))
            {
                return emptySet();
            }
            return Set.of(version);
        }
        if (versionStrings.length == 2)
        {
            final ComparableVersion minVersion = new ComparableVersion(versionStrings[0]);
            final ComparableVersion maxVersion = new ComparableVersion(versionStrings[1]);

            return inventoryReportResources
                .keySet()
                .stream()
                .filter(v -> new ComparableVersion(v).compareTo(minVersion) >= 0)
                .filter(v -> new ComparableVersion(v).compareTo(maxVersion) <= 0)
                .collect(toCollection(() -> new TreeSet<>(comparing(ComparableVersion::new))));
        }

        throw new RuntimeException("Failed to parse version range: " + alfrescoVersionRange);
    }

    public boolean isKnown(final String alfrescoVersion)
    {
        return inventoryReportResources.containsKey(alfrescoVersion);
    }
}
