/*
 * Copyright 2021 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
