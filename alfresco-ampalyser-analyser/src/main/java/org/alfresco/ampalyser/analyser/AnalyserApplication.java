/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser;

import org.alfresco.ampalyser.inventory.AlfrescoWarInventory;
import org.alfresco.ampalyser.inventory.InventoryApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@ComponentScan(
	basePackages = {"org.alfresco.ampalyser.inventory", "org.alfresco.ampalyser.analyser"},
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {AlfrescoWarInventory.class, InventoryApplication.class })
	})
public class AnalyserApplication
{
	public static void main(String[] args) {
		SpringApplication.run(AnalyserApplication.class, args);
	}

	@Bean
	public ObjectMapper objectMapper()
	{
		return new ObjectMapper();
	}
}
