package org.alfresco.ampalyser.util;

import java.io.File;
import java.nio.file.FileSystems;

public class TestResource
{
        static final String WAR_PATH = "testdata/";
        static final String DEFAULT_INV_REPORT_PATH = "./inventoryTest.inventory.json";
        static final String TARGET_PATH = "./target";

        public static String getTestResourcePath(String filename)
        {
                return TestResource.class.getClassLoader().getResource(WAR_PATH + filename).getPath();
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
