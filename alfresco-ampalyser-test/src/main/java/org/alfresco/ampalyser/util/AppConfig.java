package org.alfresco.ampalyser.util;

import org.alfresco.ampalyser.models.Command;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan("org.alfresco.ampalyser.command")
@ComponentScan("org.alfresco.ampalyser.util")
@PropertySource(value="application.properties")
public class AppConfig
{
        static final String JAVA_COM = "java -jar";

        @Bean
        public Command initInventoryCommand(@Value("${ampalyser.inventory.path}") String pathToInventoryJar)
        {
                List<String> comOptions = new ArrayList<>(Arrays.asList(JAVA_COM.split(" ")));
                comOptions.add(pathToInventoryJar);

                Command comm = new Command(comOptions);
                return comm;
        }
}
