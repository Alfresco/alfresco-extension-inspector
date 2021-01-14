/*
 * Copyright 2021 Alfresco Software, Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.alfresco.extension_inspector.analyser.checker;

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonMap;
import static org.alfresco.extension_inspector.analyser.service.ExtensionResourceInfoService.findMostSpecificMapping;
import static org.alfresco.extension_inspector.model.Resource.Type.FILE;

import java.util.Map;
import java.util.stream.Stream;

import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.result.FileOverwriteConflict;
import org.alfresco.extension_inspector.analyser.service.ConfigService;
import org.alfresco.extension_inspector.analyser.service.ExtensionResourceInfoService;
import org.alfresco.extension_inspector.model.FileResource;
import org.alfresco.extension_inspector.model.InventoryReport;
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
            .getResources().getOrDefault(FILE, emptySet())
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
