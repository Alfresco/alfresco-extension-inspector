/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static org.alfresco.ampalyser.analyser.service.ExtensionResourceInfoService.findMostSpecificMapping;
import static org.alfresco.ampalyser.model.Resource.Type.FILE;

import java.util.Map;
import java.util.stream.Stream;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.FileOverwriteConflict;
import org.alfresco.ampalyser.analyser.service.ConfigService;
import org.alfresco.ampalyser.analyser.service.ExtensionResourceInfoService;
import org.alfresco.ampalyser.model.FileResource;
import org.alfresco.ampalyser.model.InventoryReport;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Checker for detecting File Overwrites conflicts.
 *
 * @author Lucian Tuca
 */
@Component
public class FileOverwritingChecker implements Checker
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileOverwritingChecker.class);

    @Autowired
    private ConfigService configService;
    @Autowired
    private ExtensionResourceInfoService extensionResourceInfoService;

    @Override
    public Stream<Conflict> processInternal(final InventoryReport warInventory, final String alfrescoVersion)
    {
        final Map<String, String> fileMappings = configService.getFileMappings();

        final Map<String, FileResource> resourcesByDestination =
            extensionResourceInfoService.retrieveFilesByDestination();

        return warInventory
            .getResources().getOrDefault(FILE, emptyList())
            .stream()
            .map(r -> (FileResource) r)
            .filter(wr -> resourcesByDestination.containsKey(wr.getId()))
            .map(wr -> new FileOverwriteConflict(
                resourcesByDestination.get(wr.getId()),
                wr,
                computeMapping(resourcesByDestination.get(wr.getId()), fileMappings),
                alfrescoVersion
            ));
    }

    private static Map<String, String> computeMapping(final FileResource resource,
        final Map<String, String> fileMappings)
    {
        // Find the most specific/deepest mapping that we can use
        final String matchingSourceMapping = findMostSpecificMapping(fileMappings, resource);

        return matchingSourceMapping.isEmpty() ?
               null :
               singletonMap(matchingSourceMapping, fileMappings.get(matchingSourceMapping));
    }

    @Override
    public boolean canProcess(final InventoryReport warInventory, final String alfrescoVersion)
    {
        return "amp".equalsIgnoreCase(FileUtils.getExtension(configService.getExtensionPath())) &&
               !configService.getFileMappings().isEmpty();
    }
}
