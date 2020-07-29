/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.service;

import java.io.FileInputStream;
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

    private static final String DEFAULT_BEAN_OVERRIDE_WHITELIST = "/bean-overriding-whitelist.default.json";
    private static final String DEFAULT_BEAN_CLASS_WHITELIST = "/bean-restricted-classes-whitelist.default.json";
    private static final String DEFAULT_3RD_PARTY_ALLOWEDLIST = "/restricted-3rd-party-classes-allowedlist.default.json";

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Reads and loads whitelist for the beans in a war that can be overridden when an .amp is applied
     *
     * @return a {@link Set} of the whitelisted beans (that can be overridden).
     */
    public Set<String> loadBeanOverrideWhitelist(final String path)
    {
        final Set<String> whitelist = new HashSet<>();

        try
        {
            whitelist.addAll(objectMapper.readValue(
                getClass().getResourceAsStream(DEFAULT_BEAN_OVERRIDE_WHITELIST),
                new TypeReference<>() {}));
        }
        catch (IOException ioe)
        {
            LOGGER.error("Failed to read DEFAULT Bean Overriding Whitelist file: " + DEFAULT_BEAN_OVERRIDE_WHITELIST,
                ioe);
            throw new RuntimeException(
                "Failed to read DEFAULT Bean Overriding Whitelist file: " + DEFAULT_BEAN_OVERRIDE_WHITELIST, ioe);
        }

        if (path == null)
        {
            return whitelist;
        }

        try
        {
            whitelist.addAll(objectMapper.readValue(
                new FileInputStream(path),
                new TypeReference<>() {}));
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to read Bean Overriding Whitelist file: " + path, e);
            throw new RuntimeException("Failed to read Bean Overriding Whitelist file: " + path, e);
        }

        return whitelist;
    }

    /**
     * Reads and loads a class whitelist for the .amp beans from a .json file
     *
     * @return a {@link Set} of the whitelisted beans (that can be overridden).
     */
    public Set<String> loadBeanClassWhitelist(final String path)
    {
        final Set<String> whitelist = new HashSet<>();

        try
        {
            whitelist.addAll(objectMapper.readValue(
                getClass().getResourceAsStream(DEFAULT_BEAN_CLASS_WHITELIST),
                new TypeReference<>() {}));
        }
        catch (IOException ioe)
        {
            LOGGER.error(
                "Failed to read DEFAULT Bean Restricted Classes Whitelist file: " + DEFAULT_BEAN_CLASS_WHITELIST,
                ioe);
            throw new RuntimeException(
                "Failed to read DEFAULT Bean Restricted Whitelist file: " + DEFAULT_BEAN_CLASS_WHITELIST,
                ioe);
        }

        if (path == null)
        {
            return whitelist;
        }

        try
        {
            whitelist.addAll(objectMapper.readValue(
                new FileInputStream(path),
                new TypeReference<>() {}));
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to read Bean Restricted Classes Whitelist file: " + path, e);
            throw new RuntimeException(
                "Failed to read Bean Restricted Classes Whitelist file: " + path, e);
        }

        return whitelist;
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
