/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.worker;

import static java.util.Collections.emptyList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.alfresco.ampalyser.inventory.EntryProcessor;
import org.alfresco.ampalyser.inventory.model.BeanResource;
import org.alfresco.ampalyser.inventory.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
public class BeanInventoryWorker implements InventoryWorker
{
    private static final Logger LOG = LoggerFactory.getLogger(BeanInventoryWorker.class);

    public BeanInventoryWorker(EntryProcessor processor)
    {
        processor.attach(this);
    }

    @Override
    public List<Resource> processInternal(ZipEntry zipEntry, byte[] data, String definingObject)
    {
        String xmlFileName = zipEntry.getName();
        try
        {
            return analyseXmlFile(data, xmlFileName);
        }
        catch (IOException ioe)
        {
            LOG.warn("Failed to open and read from xml file: " + xmlFileName);
            return emptyList();
        }
        catch (Exception e)
        {
            LOG.warn("Failed to analyse beans in xml file: " + xmlFileName);
            return emptyList();
        }
    }

    @Override
    public Resource.Type getType()
    {
        return Resource.Type.BEAN;
    }

    @Override
    public boolean canProcessEntry(ZipEntry entry, String definingObject)
    {
        return entry != null
            && entry.getName() != null
            && entry.getName().toLowerCase().endsWith(".xml");
    }

    /**
     *
     * @param xmlData
     * @param definingObject
     * @return
     * @throws Exception
     */
    private List<Resource> analyseXmlFile(byte[] xmlData, String definingObject)
        throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(xmlData));

        if (doc.getDocumentElement().getNodeName().equals("beans"))
        {
            if (!definingObject.startsWith("alfresco/subsystems/"))
            {
                return findBeans(doc.getDocumentElement(), definingObject);
            }
        }
        return emptyList();
    }


    private List<Resource> findBeans(Element docElem, String definingObject)
    {
        List<Resource> foundBeans = new ArrayList<>();

        NodeList nodeList = docElem.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Node node = nodeList.item(i);
            if (node instanceof Element)
            {
                Element elem = (Element) node;
                if (elem.getTagName().equals("bean"))
                {
                    String beanId = elem.getAttribute("id");
                    String beanName = elem.getAttribute("name");
                    if (beanId == null || beanId.trim().length() == 0)
                    {
                        beanId = beanName;
                    }
                    if (beanId == null || beanId.trim().length() == 0)
                    {
                        LOG.warn("Found anonymous bean in XML resource " + definingObject);
                    }
                    else
                    {
                        BeanResource beanResource = new BeanResource(beanId, beanName, definingObject);

                        foundBeans.add(beanResource);
                        LOG.debug("Added bean: " + beanId + " found in: " + definingObject);
                    }
                }
            }
        }

        return foundBeans;
    }
}
