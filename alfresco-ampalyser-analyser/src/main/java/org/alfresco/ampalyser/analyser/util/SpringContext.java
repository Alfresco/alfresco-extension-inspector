/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring Context Holder for usage in static contexts or in non-bean objects.
 */
public class SpringContext implements ApplicationContextAware
{
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
    {
        context = applicationContext;
    }

    public static <T> T getBean(final Class<T> clazz)
    {
        return context.getBean(clazz);
    }

    public static <T> T getBean(final String name, final Class<T> clazz)
    {
        return context.getBean(name, clazz);
    }
}
