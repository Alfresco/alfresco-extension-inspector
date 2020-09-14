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
        static final String JAVA_COM = "java -jar";

        @Bean
        public CommandModel initInventoryCommand(@Value("${extension_inspector.inventory.path}") String pathToInventoryJar)
        {
                return addPathToCommandOptions(pathToInventoryJar);
        }

        @Bean
        public CommandModel initAnalyserCommand(@Value("${extension_inspector.analyser.path}") String pathToAnalyserAmp)
        {
                return addPathToCommandOptions(pathToAnalyserAmp);
        }

        @Bean
        public ObjectMapper mapper()
        {
                return new ObjectMapper();
        }

        public CommandModel addPathToCommandOptions(String path)
        {
                List<String> comOptions = new ArrayList<>(Arrays.asList(JAVA_COM.split(" ")));
                comOptions.add(path);

                CommandModel cmd = new CommandModel(comOptions);
                return cmd;
        }
}
