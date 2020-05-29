package org.alfresco.ampalyser.analyser;

import javax.annotation.PostConstruct;

import org.alfresco.ampalyser.analyser.parser.InventoryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Lucian Tuca
 * created on 29/05/2020
 */
@Component
public class JustATestclass
{
    @Autowired
    private InventoryParser jsonInventoryParser;

    @PostConstruct
    public void doStuff()
    {
        jsonInventoryParser.parseReport("/Users/p3700621/Alfresco/amp-a-lyser/report.json");
    }
}
