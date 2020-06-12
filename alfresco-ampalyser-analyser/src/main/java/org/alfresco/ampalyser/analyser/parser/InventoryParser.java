/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.parser;

import java.io.InputStream;

import org.alfresco.ampalyser.model.InventoryReport;

/**
 * @author Lucian Tuca
 */
public interface InventoryParser
{
    InventoryReport parseReport(String path);

    InventoryReport parseReport(InputStream is);
}
