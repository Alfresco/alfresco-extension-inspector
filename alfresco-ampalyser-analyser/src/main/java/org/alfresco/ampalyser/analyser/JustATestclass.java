package org.alfresco.ampalyser.analyser;

import javax.annotation.PostConstruct;

import org.alfresco.ampalyser.analyser.parser.InventoryParser;
import org.alfresco.ampalyser.model.InventoryReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Lucian Tuca
 */
@Component
public class JustATestclass
{
    @Autowired
    private InventoryParser jsonInventoryParser;

    @PostConstruct
    public void doStuff()
    {
        InventoryReport inventoryReport = jsonInventoryParser
            .parseReport("/Users/p3700621/Alfresco/amp-a-lyser/alfresco.inventory.json");
        System.out.println(inventoryReport);
    }
}
