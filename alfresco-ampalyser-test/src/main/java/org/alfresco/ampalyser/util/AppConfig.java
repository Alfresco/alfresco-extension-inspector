package org.alfresco.ampalyser.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alfresco.ampalyser.models.InventoryCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan("org.alfresco.ampalyser")
@PropertySource(value="application.properties")
public class AppConfig
{
        static final String JAVA_COM = "java -jar";

        @Bean
        public InventoryCommand initInventoryCommand(@Value("${ampalyser.inventory.path}") String pathToInventoryJar)
        {
                List<String> comOptions = new ArrayList<>(Arrays.asList(JAVA_COM.split(" ")));
                comOptions.add(pathToInventoryJar);

                InventoryCommand cmd = new InventoryCommand(comOptions);
                return cmd;
        }

        @Bean
        public ObjectMapper mapper()
        {
                return new ObjectMapper();
        }
}
