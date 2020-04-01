package org.alfresco.ampalyser.inventory;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AlfrescoWarInventory {

    public static void main(String[] args) {
        (new AlfrescoWarInventory()).run(args);
    }

    boolean printProblems = true;

    ArrayList<String> allResources = new ArrayList<>(1000);

    HashMap<String,String> classPathElements = new HashMap<>(100000);

    HashMap<String,String> beanDefs = new HashMap<>(100000);

    ArrayList<String> alfrescoPublicApi = new ArrayList<>(1000);

    void run(String[] args) {
        if(args.length != 1) {
            printUsage();
            return;
        }
        FileInputStream fis;
        try {
            fis = new FileInputStream(args[0]);
        } catch(Exception e) {
            System.err.println("Failed opening file "+args[0]);
            e.printStackTrace(System.err);
            return;
        }
        ZipInputStream zis;
        try {
            zis = new ZipInputStream(fis);
        } catch(Exception e) {
            try {
                fis.close();
            } catch(Exception ee) {
                //
            }
            System.err.println("Failed opening web archive "+args[0]);
            e.printStackTrace(System.err);
            return;
        }
        try {
            ZipEntry ze = zis.getNextEntry();
            while(ze != null) {
                addToInventory(zis, ze);
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
        } catch(Exception e) {
            System.err.println("Failed reading web archive "+args[0]);
            e.printStackTrace(System.err);
        } finally {
            try {
                zis.close();
            } catch(Exception e) {
                //
            }
        }
        printResults();
    }

    private void printUsage() {
        System.out.println("usage:");
        System.out.println("java -jar alfresco-war-inventory.jar <alfresco-war-filename>");
    }

    private void printResults() {
        System.out.println("resources: "+allResources.size());
        System.out.println("classpath: "+classPathElements.size());
        System.out.println("beans: "+beanDefs.size());
        System.out.println("AlfrescoPublicApi: "+alfrescoPublicApi.size());
    }

    private void addToInventory(ZipInputStream zis, ZipEntry webArchiveElement)  throws Exception {
        if(webArchiveElement.isDirectory()) {
            return;
        }
        String resourceName = webArchiveElement.getName();
        allResources.add(resourceName);
        if(resourceName.startsWith("WEB-INF/lib/")) {
            addLibraryToClasspath(zis, webArchiveElement);
        } else if(resourceName.startsWith("WEB-INF/classes/")) {
            addFileToClasspath(zis, webArchiveElement, false, resourceName);
        }
    }

    private void addLibraryToClasspath(ZipInputStream zis, ZipEntry webArchiveElement) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(extract(zis));
        ZipInputStream libZis = new ZipInputStream(bis);
        ZipEntry libZe = libZis.getNextEntry();
        while(libZe != null) {
            if(!(libZe.isDirectory() || libZe.getName().startsWith("META-INF/") || libZe.getName().equals("module-info.class") || libZe.getName().equalsIgnoreCase("license.txt") || libZe.getName().equalsIgnoreCase("notice.txt"))) {
                addFileToClasspath(libZis, libZe, true, webArchiveElement.getName());
            }
            libZis.closeEntry();
            libZe = libZis.getNextEntry();
        }
    }

    private void addFileToClasspath(ZipInputStream zis, ZipEntry webArchiveElement, boolean inLib, String definingObject) throws Exception {
        String resourceName = webArchiveElement.getName();
        if(!inLib) {
            resourceName = resourceName.substring("WEB-INF/classes/".length());
        }
        if(classPathElements.containsKey(resourceName)) {
            if(printProblems) {
                System.out.println("Duplicate classpath entry: "+resourceName);
                System.out.println("    "+definingObject);
                System.out.println("    "+classPathElements.get(resourceName));
            }
        } else {
            classPathElements.put(resourceName, definingObject);
        }
        if(resourceName.endsWith(".xml")) {
            byte[] data = extract(zis);
            try {
                analyseXmlFile(data, resourceName, definingObject);
            } catch(Exception e) {
                System.out.println("Failed reading XML resource "+resourceName+" in "+definingObject);
            }
        } else if(resourceName.endsWith(".class")) {
            byte[] data = extract(zis);
            try {
                analyseClassFile(data, resourceName, definingObject);
            } catch(Exception e) {
                System.out.println("Failed reading Class resource "+resourceName+" in "+definingObject);
            }
        }
    }

    private byte[] extract(ZipInputStream zis) throws Exception {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len;
        while ((len = zis.read(buffer)) > 0) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    private void analyseXmlFile(byte[] xmlData, String resourceName, String definingObject) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(xmlData));
        if(doc.getDocumentElement().getNodeName().equals("beans")) {
            if(resourceName.startsWith("alfresco/subsystems/")) {

            } else {
                analyseBeans(doc.getDocumentElement(), resourceName, definingObject);
            }
        }
    }

    private void analyseBeans(Element docElem, String resourceName, String definingObject) {
        NodeList nodeList = docElem.getChildNodes();
        for(int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if(n instanceof Element) {
                Element elem = (Element)n;
                if(elem.getTagName().equals("bean")) {
                    String beanId = elem.getAttribute("id");
                    String beanName = elem.getAttribute("name");
                    if(beanId == null || beanId.trim().length()==0) {
                        beanId = beanName;
                    }
                    if(beanId == null || beanId.trim().length()==0) {
                        //System.out.println("WARN: Found anonymous bean in XML resource "+resourceName+" in "+definingObject);
                    } else {
                        if(beanDefs.containsKey(beanId)) {
                            if(printProblems) {
                                System.out.println("Duplicate bean def: "+beanId);
                                System.out.println("    "+resourceName+"@"+definingObject);
                                System.out.println("    "+beanDefs.get(beanId));
                            }
                        } else {
                            beanDefs.put(beanId, resourceName+"@"+definingObject);
                        }
                    }
                }
            }
        }
    }

    private void analyseClassFile(byte[] classData, String resourceName, String definingObject) throws Exception {
        ClassParser cp = new ClassParser(new ByteArrayInputStream(classData), null);
        JavaClass jc = cp.parse();
        AnnotationEntry[] aes = jc.getAnnotationEntries();
        boolean isAlfrescoPublicApi = false;
        boolean isDeprecated = false;
        if(aes != null && aes.length > 0) {
            for(AnnotationEntry ae: aes) {
                if(ae.getAnnotationType().equals("Lorg/alfresco/api/AlfrescoPublicApi;")) {
                    isAlfrescoPublicApi = true;
                }
                if(ae.getAnnotationType().equals("Ljava/lang/Deprecated;")) {
                    isDeprecated = true;
                }
            }
        }
        if(isAlfrescoPublicApi) {
            if(isDeprecated) {
                System.out.println("Deprecated AlfrescoPublicApi: "+jc.getClassName());
            } else {
                alfrescoPublicApi.add(jc.getClassName());
            }
        }
    }

}
