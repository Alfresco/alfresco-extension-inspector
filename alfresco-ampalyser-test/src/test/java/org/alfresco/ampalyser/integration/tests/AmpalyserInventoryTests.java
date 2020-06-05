/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.integration.tests;

import org.alfresco.ampalyser.AmpalyserClient;
import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.util.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import java.io.File;

@ContextConfiguration(classes = AppConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AmpalyserInventoryTests extends AbstractTestNGSpringContextTests
{
        protected static final String SUCCESS_MESSAGE = "Inventory report generated";
        protected static final String INVALID_XML_MESSAGE = "Failed to analyse beans in xml file: ";
        protected static final String INVALID_XML = "alfresco/invalidXML.xml";

        @Autowired
        protected AmpalyserClient client;

        protected File inventoryReport;
        protected CommandOutput cmdOut;
}
