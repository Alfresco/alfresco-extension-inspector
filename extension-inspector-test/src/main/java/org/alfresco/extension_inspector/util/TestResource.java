/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
