/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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
import static org.alfresco.extension_inspector.model.Resource.Type.BEAN;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.alfresco.extension_inspector.analyser.result.BeanOverwriteConflict;
import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.service.ConfigService;
import org.alfresco.extension_inspector.analyser.service.ExtensionResourceInfoService;
import org.alfresco.extension_inspector.model.BeanResource;
import org.alfresco.extension_inspector.model.InventoryReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Lucian Tuca
 */
@Component
public class BeanOverwritingChecker implements Checker
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanOverwritingChecker.class);

    @Autowired
    private ConfigService configService;
    @Autowired
    private ExtensionResourceInfoService extensionResourceInfoService;

    @Override
    public Stream<Conflict> processInternal(final InventoryReport warInventory, final String alfrescoVersion)
    {
        final Map<String, Set<BeanResource>> resourcesById = extensionResourceInfoService.retrieveBeanOverridesById();

        // Find a list of possible conflicts (there's no way to know for sure) for each amp bean resource
        return warInventory
            .getResources().getOrDefault(BEAN, emptySet())
            .stream()
            .map(r -> (BeanResource) r)
            .filter(wr -> resourcesById.containsKey(wr.getId()))
            .flatMap(wr -> resourcesById
                .get(wr.getId())
                .stream()
                .map(r -> new BeanOverwriteConflict(r, wr, alfrescoVersion)));
    }

    @Override
    public boolean canProcess(final InventoryReport warInventory, final String alfrescoVersion)
    {
        return configService.getBeanOverrideAllowedList() != null;
    }
}
