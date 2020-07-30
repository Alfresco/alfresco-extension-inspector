/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.service;

import java.io.IOException;
import java.util.Set;

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
}
