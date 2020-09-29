/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.extension_inspector;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;

import org.alfresco.extension_inspector.analyser.parser.InventoryParser;
import org.alfresco.extension_inspector.model.InventoryReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApplicationContextTests
{
	@Autowired
	InventoryParser jsonInventoryParser;

	@Test
	void contextLoads()
	{
	}

	@Test
	void basicDeserializationTest()
	{
		// We're using the report generated (manually) by the inventory tool on the 'test.war' resource
		File file = new File(getClass().getClassLoader().getResource("test.inventory.json").getFile());

		InventoryReport inventoryReport = jsonInventoryParser.parseReport(file.getAbsolutePath());

		assertNotNull(inventoryReport);
	}
}
