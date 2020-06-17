/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.checker;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.alfresco.ampalyser.model.Resource.Type.BEAN;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.analyser.result.Conflict;
import org.alfresco.ampalyser.analyser.result.RestrictedBeanClassConflict;
import org.alfresco.ampalyser.model.BeanResource;
import org.alfresco.ampalyser.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Lucian Tuca
 */
@Component
public class BeanRestrictedClassesChecker implements Checker
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanRestrictedClassesChecker.class);

    private static final String RESTRICTED_BEANS_WHITELIST = "restricted-beans-whitelist.txt";
    private static final String ORG_ALFRESCO_PREFIX = "org.alfresco";

    @Override
    public List<Conflict> processInternal(Collection<Resource> ampResources,
        Collection<Resource> warResources, Map<String, Object> extraInfo)
    {
        Set<String> whitelistedClasses = loadWhitelistedClassesFromFile();
        List<Conflict> conflicts = new LinkedList<>();

        List<Conflict> alfrescoRestrictedInstantiatons = ampResources.stream()
                .filter(ampR -> (ampR instanceof BeanResource
                    && ((BeanResource) ampR).getBeanClass() != null)
                    && ((BeanResource) ampR).getBeanClass().startsWith(ORG_ALFRESCO_PREFIX))
                .filter(ampR -> whitelistedClasses
                    .stream()
                    .noneMatch(entry -> entry.contains(((BeanResource) ampR).getBeanClass())))
                .map(ampR -> new RestrictedBeanClassConflict(ampR, null, (String) extraInfo.get(ALFRESCO_VERSION)))
            .collect(toList());

        // TODO: Add some more conflict checking based on lists provided by the user?

        conflicts.addAll(alfrescoRestrictedInstantiatons);


        return conflicts;
    }


    @Override
    public boolean canProcess(Collection<Resource> ampResources, Collection<Resource> warResources,
        Map<String, Object> extraInfo)
    {
        return ampResources.stream().allMatch(r -> BEAN == r.getType())
            && warResources.stream().allMatch(r -> BEAN == r.getType())
            && extraInfo != null;
    }

    @Override
    public Resource.Type resourceType()
    {
        return BEAN;
    }

    private static Set<String> loadWhitelistedClassesFromFile()
    {
        try
        {
            return Files.lines(Paths.get(ClassLoader.getSystemResource(RESTRICTED_BEANS_WHITELIST).toURI()))
                .collect(toSet());
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to read Restricted Beans classes Whitelist file", e);
            throw new RuntimeException("Failed to read Restricted Beans classes Whitelist file", e);
        }
    }
}
