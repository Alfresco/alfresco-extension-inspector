/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.service;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableMap;
import static org.alfresco.ampalyser.model.Resource.Type.FILE;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.inventory.service.InventoryService;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Single source of truth for the ampalyser-analyser execution.
 * It contains the runtime configuration provided through the application arguments.
 * It can also server as a global cache for resources that don't change during the
 * application's runtime (e.g. AMP information - file mappings)
 */
@Service
public class ConfigService
{
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private FileMappingService fileMappingService;
    @Autowired
    private AllowedListService allowedListService;

    private String extensionPath;
    private Map<Resource.Type, Set<Resource>> extensionResources = new EnumMap<>(Resource.Type.class);
    private Map<String, String> fileMappings = emptyMap();
    private Set<String> beanOverrideAllowedList = emptySet();
    private Set<String> internalClassAllowedList = emptySet();
    private Set<String> thirdPartyAllowedList = emptySet();
    private boolean verboseOutput = false;

    @PostConstruct
    public void init()
    {
        beanOverrideAllowedList = allowedListService.loadBeanOverrideAllowedList();
        thirdPartyAllowedList = allowedListService.load3rdPartyAllowedList();
        internalClassAllowedList = allowedListService.loadInternalClassAllowedList();
    }
    
    public String getExtensionPath()
    {
        return extensionPath;
    }

    public Set<Resource> getExtensionResources(final Resource.Type type)
    {
        return extensionResources.getOrDefault(type, emptySet());
    }

    public Map<String, String> getFileMappings()
    {
        return fileMappings;
    }

    public Set<String> getBeanOverrideAllowedList()
    {
        return beanOverrideAllowedList;
    }

    public Set<String> getInternalClassAllowedList()
    {
        return internalClassAllowedList;
    }

    public Set<String> getThirdPartyAllowedList()
    {
        return thirdPartyAllowedList;
    }

    public boolean isVerboseOutput()
    {
        return verboseOutput;
    }

    public void setVerboseOutput(boolean verboseOutput)
    {
        this.verboseOutput = verboseOutput;
    }

    public void registerExtensionPath(final String extensionPath)
    {
        this.extensionPath = extensionPath;
        final InventoryReport inventory = inventoryService.extractInventoryReport(extensionPath);
        extensionResources = unmodifiableMap(inventory.getResources());
        fileMappings = fileMappingService.compileFileMappings(
            extensionPath, extensionResources.getOrDefault(FILE, emptySet()));
    }
}
