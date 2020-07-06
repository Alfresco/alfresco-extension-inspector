package org.alfresco.ampalyser.analyser.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
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

    private static final String DEFAULT_BEAN_OVERRIDE_WHITELIST = "/bean-overriding-whitelist.default.json";
    private static final String DEFAULT_BEAN_CLASS_BLACKLIST = "/bean-restricted-classes-whitelist.default.json";

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
    public Set<String> loadWhitelistBeanRestrictedClasses(final String path)
    {
        final Set<String> whitelist = new HashSet<>();

        try
        {
            whitelist.addAll(objectMapper.readValue(
                getClass().getResourceAsStream(DEFAULT_BEAN_CLASS_BLACKLIST),
                new TypeReference<>() {}));
        }
        catch (IOException ioe)
        {
            LOGGER.error(
                "Failed to read DEFAULT Bean Restricted Classes Whitelist file: " + DEFAULT_BEAN_CLASS_BLACKLIST,
                ioe);
            throw new RuntimeException(
                "Failed to read DEFAULT Bean Restricted Whitelist file: " + DEFAULT_BEAN_CLASS_BLACKLIST,
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
}
