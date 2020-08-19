/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser;

import static java.lang.String.join;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Stream.concat;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.alfresco.ampalyser.command.AnalyserCommandReceiver;
import org.alfresco.ampalyser.model.InventoryReport;
import org.alfresco.ampalyser.model.Resource;
import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.util.JsonInventoryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InventoryClient
{
    @Value("${ampalyser.inventory.path}")
    private String pathToInventoryJar;

    @Autowired
    private AnalyserCommandReceiver commReceiver;

    @Autowired
    private JsonInventoryParser jsonInventory;

    public CommandOutput runCommand(List<String> cmdOptions)
    {
        // combine the java -jar command and the given options
        final List<String> commandAndOptions = concat(
            Stream.of("java", "-jar", pathToInventoryJar),
            cmdOptions.stream()
        ).collect(toUnmodifiableList());

        System.out.println("Running command: " + join(" ", commandAndOptions));
        return commReceiver.runAnalyserCmd(commandAndOptions);
    }

    public Resource retrieveInventoryResource(Resource.Type resourceType, String resourceId, File jsonReport)
    {
        final InventoryReport inventoryReport = jsonInventory.getInventoryReportFromJson(jsonReport);

        return inventoryReport
            .getResources().get(resourceType)
            .stream()
            .filter(resource -> resource.getId().equals(resourceId))
            .findFirst()
            .orElse(null);
    }

    public Set<Resource> retrieveInventoryResources(Resource.Type resourceType, File jsonReport)
    {
        final InventoryReport inventoryReport = jsonInventory.getInventoryReportFromJson(jsonReport);

        return inventoryReport.getResources().get(resourceType);
    }
}
