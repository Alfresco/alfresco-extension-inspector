/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.extension_inspector.util;

import java.io.File;
import java.nio.file.FileSystems;

public class TestResource
{
    public static final String SUCCESS_MESSAGE = "Inventory report generated";
    public static final String INVALID_XML_MESSAGE = "Failed to analyse beans in xml file: ";
    public static final String INVALID_XML = "alfresco/invalidXML.xml";

    static final String TEST_RESOURCE_PATH = "testdata/";
    static final String DEFAULT_INV_REPORT_PATH = "./inventoryTest.inventory.json";
    static final String TARGET_PATH = "./target";

    public static String getTestResourcePath(String filename)
    {
        return TestResource.class.getClassLoader().getResource(TEST_RESOURCE_PATH + filename).getPath();
    }

    public static File getTestInventoryReport()
    {
        return new File(DEFAULT_INV_REPORT_PATH);
    }

    public static String getTargetPath()
    {
        return FileSystems.getDefault().getPath(TARGET_PATH).normalize().toString();
    }
}
