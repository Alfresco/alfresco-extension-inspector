package org.alfresco.ampalyser.util;

public class TestResource
{
        static final String WAR_PATH = "testdata/";

        public static String getTestResourcePath(String filename)
        {
                return TestResource.class.getClassLoader().getResource(WAR_PATH + filename).getPath();
        }
}
