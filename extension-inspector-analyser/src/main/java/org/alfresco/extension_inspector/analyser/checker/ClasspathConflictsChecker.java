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
import static org.alfresco.extension_inspector.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.alfresco.extension_inspector.analyser.result.ClasspathConflict;
import org.alfresco.extension_inspector.analyser.result.Conflict;
import org.alfresco.extension_inspector.analyser.service.ConfigService;
import org.alfresco.extension_inspector.analyser.service.ExtensionResourceInfoService;
import org.alfresco.extension_inspector.model.ClasspathElementResource;
import org.alfresco.extension_inspector.model.InventoryReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClasspathConflictsChecker implements Checker
{
    @Autowired
    private ConfigService configService;
    @Autowired
    private ExtensionResourceInfoService extensionResourceInfoService;

    @Override
    public Stream<Conflict> processInternal(final InventoryReport warInventory, final String alfrescoVersion)
    {
        final Map<String, Set<ClasspathElementResource>> elementsById =
            extensionResourceInfoService.retrieveClasspathElementsById();

        return warInventory
            .getResources().getOrDefault(CLASSPATH_ELEMENT, emptySet())
            .stream()
            .map(r -> (ClasspathElementResource) r)
            .filter(wr -> elementsById.containsKey(wr.getId()))
            .flatMap(wr -> elementsById
                .get(wr.getId())
                .stream()
                .map(r -> new ClasspathConflict(r, wr, alfrescoVersion)));
    }

    @Override
    public boolean canProcess(final InventoryReport warInventory, final String alfrescoVersion)
    {
        return !configService.getExtensionResources(CLASSPATH_ELEMENT).isEmpty() &&
               !isEmpty(warInventory.getResources().get(CLASSPATH_ELEMENT));
    }
}
