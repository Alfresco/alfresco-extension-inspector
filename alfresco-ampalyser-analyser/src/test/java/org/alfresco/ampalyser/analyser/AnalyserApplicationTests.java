package org.alfresco.ampalyser.analyser;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.alfresco.ampalyser.analyser.parser.InventoryParser;
import org.alfresco.ampalyser.model.InventoryReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AnalyserApplicationTests {

	@Autowired
	InventoryParser jsonInventoryParser;

	@Test
	void contextLoads() {
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
