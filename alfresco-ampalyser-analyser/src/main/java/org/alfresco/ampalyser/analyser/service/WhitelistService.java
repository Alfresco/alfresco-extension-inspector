/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Lucian Tuca
 */
@Component
public class WhitelistService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistService.class);

    private static final String ALLOWED_BEAN_OVERRIDE_LIST = "/allowedBeanOverrideList.json";
    private static final String ALLOWED_BEAN_CLASS_LIST = "/allowedBeanClassList.json";
    private static final String DEFAULT_3RD_PARTY_ALLOWEDLIST = "/restricted-3rd-party-classes-allowedlist.default.json";

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Reads and loads whitelist for the beans in a war that can be overridden when an .amp is applied
     *
     * @return a {@link Set} of the whitelisted beans
     */
    public Set<String> loadBeanOverrideWhitelist()
    {
        try
        {
            return objectMapper.readValue(
                getClass().getResourceAsStream(ALLOWED_BEAN_OVERRIDE_LIST),
                new TypeReference<>() {});
        }
        catch (IOException ioe)
        {
            LOGGER.error("Failed to read Allowed Bean Overriding List file: " + ALLOWED_BEAN_OVERRIDE_LIST,
                ioe);
            throw new RuntimeException(
                "Failed to read Allowed Bean Overriding List file: " + ALLOWED_BEAN_OVERRIDE_LIST, ioe);
        }
    }

    /**
     * Reads and loads a class whitelist for the .amp beans from a .json file
     *
     * @return a {@link Set} of the whitelisted beans
     */
    public Set<String> loadBeanClassWhitelist()
    {
        try
        {
            return objectMapper.readValue(
                getClass().getResourceAsStream(ALLOWED_BEAN_CLASS_LIST),
                new TypeReference<>() {});
        }
        catch (IOException ioe)
        {
            LOGGER.error(
                "Failed to read Allowed Bean Restricted Class List file: " + ALLOWED_BEAN_CLASS_LIST,
                ioe);
            throw new RuntimeException(
                "Failed to read Allowed Bean Restricted List file: " + ALLOWED_BEAN_CLASS_LIST,
                ioe);
        }
    }

    /**
     * Reads and loads a 3rd party allowed list for the .amp classes to use from a .json file
     *
     * @return a {@link Set} of the whitelisted beans (that can be overridden).
     */
    public Set<String> load3rdPartyAllowedList()
    {
        final Set<String> allowedList;

        try
        {
            // Allow the user to input a friendly format, e.g. 'org.alfresco.repo.*' and convert it internally the '/' delimited structure
            allowedList = new HashSet<String>(objectMapper.readValue(getClass().getResourceAsStream(DEFAULT_3RD_PARTY_ALLOWEDLIST),
                new TypeReference<>() {})).stream()
                .map(s -> s.replaceAll("\\.\\*", "").replaceAll("\\.", "/"))
                .collect(Collectors.toSet());
        }
        catch (IOException ioe)
        {
            LOGGER.error(
                "Failed to read DEFAULT 3rd Party Restricted Classes Whitelist file: " + DEFAULT_3RD_PARTY_ALLOWEDLIST, ioe);
            throw new RuntimeException(
                "Failed to read DEFAULT 3rd Party Restricted Classes Whitelist file: " + DEFAULT_3RD_PARTY_ALLOWEDLIST, ioe);
        }

        return allowedList;
    }
}
