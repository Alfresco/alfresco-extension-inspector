/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.inventory.worker;

import static java.util.Collections.emptySet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;

import org.alfresco.ampalyser.model.BeanResource;
import org.alfresco.ampalyser.model.Resource;
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

    @Override
    public Set<Resource> processInternal(ZipEntry zipEntry, byte[] data, String definingObject)
    {
        String filename = zipEntry.getName();

        try
        {
            return analyseXmlFile(data, filename, definingObject);
        }
        catch (IOException ioe)
        {
            LOG.warn("Failed to open and read from xml file: " + filename);
            return emptySet();
        }
        catch (Exception e)
        {
            LOG.warn("Failed to analyse beans in xml file: " + filename);
            return emptySet();
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
     * Analyses a .xml file, looking for Alfresco beans
     *
     * @param xmlData the content of the .xml file
     * @param filename the name of the .xml file
     * @param definingObject the name of the parent of the .xml file (e.g. the .jar)
     *
     * @return a set of {@link BeanResource} found in the .xml file
     *
     * @throws Exception
     */
    private Set<Resource> analyseXmlFile(byte[] xmlData, String filename, String definingObject)
        throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(xmlData));

        if (doc.getDocumentElement().getNodeName().equals("beans"))
        {
            if (!definingObject.startsWith("alfresco/subsystems/"))
            {
                return findBeans(doc.getDocumentElement(), filename, definingObject);
            }
        }
        return emptySet();
    }

    /**
     * Builds a set of Alfresco {@link BeanResource} objects that are present in the pr
     *
     * @param docElem the 'beans' tag in the .xml file
     * @param filename the .xml filename
     * @param definingObject the name of the parent of the .xml file (e.g. the .jar)
     *
     * @return a set of {@link BeanResource} found in the .xml file
     */
    private Set<Resource> findBeans(Element docElem, String filename, String definingObject)
    {
        final Set<Resource> foundBeans = new LinkedHashSet<>();

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
                    String beanClass = elem.getAttribute("class");

                    // If the bean does not have an id, use the name
                    if (beanId == null || beanId.trim().length() == 0)
                    {
                        beanId = beanName;
                    }

                    // If the bean does not have an id or a name, use the class
                    if (beanId == null || beanId.trim().length() == 0)
                    {
                        if (LOG.isTraceEnabled())
                        {
                            LOG.trace("Found anonymous bean in XML resource " + definingObject);
                            LOG.trace("Falling back and setting the id as the class: " + beanClass);
                        }

                        // The code might find beans that have none of the id/name/class defined. Tough luck
                        // The parent tag might be present and could be used.

                        beanId = beanClass;
                    }

                    // Anonymous beans will not be added to the report.
                    if (beanId != null && !beanId.isEmpty())
                    {
                        BeanResource beanResource = new BeanResource(
                            beanId,
                            definingObject.endsWith("jar") ? filename + "@" + definingObject : filename,
                            beanClass);
                        foundBeans.add(beanResource);

                        if (LOG.isTraceEnabled())
                        {
                            LOG.trace("Added bean: " + beanId + " found in file: " + filename + " parent: " + definingObject);
                        }
                    }
                }
            }
        }

        return foundBeans;
    }
}
