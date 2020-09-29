/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.extension_inspector.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alfresco.extension_inspector.models.CommandModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan("org.alfresco.extension_inspector")
@PropertySource(value="application.properties")
public class AppConfig
{
        static final List<String> JAVA_COM =  Arrays.asList("java -jar".split(" "));

        @Bean
        public CommandModel initInventoryCommand(@Value("${extension_inspector.path}") String path)
        {
                final List<String> comOptions = new ArrayList<>(JAVA_COM);
                comOptions.add(path);
                comOptions.add("--inventory");

                return new CommandModel(comOptions);
        }

        @Bean
        public CommandModel initAnalyserCommand(@Value("${extension_inspector.path}") String path)
        {
                final List<String> comOptions = new ArrayList<>(JAVA_COM);
                comOptions.add(path);

                return new CommandModel(comOptions);
        }

        @Bean
        public ObjectMapper mapper()
        {
                return new ObjectMapper();
        }
}
