/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.service;

/**
 * Main service for analysing the possible conflicts that can occur when an amp is applied.
 *
 * @author Lucian Tuca
 */
public interface AnalysingService
{
    // TODO: Update to ExitCodeGenerator in ACS-285
    public int analyse(String ampPath, String warInventoryReport);
}
