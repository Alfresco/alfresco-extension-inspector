package org.alfresco.ampalyser.integration.tests;

import org.alfresco.ampalyser.AmpalyserClient;

import java.io.File;

public class AmpalyserInventoryTests
{
        protected static final String PUBLIC_API_TYPE = "ALFRESCO_PUBLIC_API";
        protected static final String BEAN_TYPE = "BEAN";
        protected static final String CLASSPATH_ELEMENT_TYPE = "CLASSPATH_ELEMENT";
        protected static final String FILE_TYPE = "FILE";

        protected static final String SUCCESS_MESSAGE = "Inventory report generated";
        protected static final String INVALID_XML_MESSAGE = "Failed to analyse beans in xml file: ";
        protected static final String INVALID_XML = "alfresco/invalidXML.xml";

        protected AmpalyserClient client = new AmpalyserClient();

        protected File inventoryReport;
}
