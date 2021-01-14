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
