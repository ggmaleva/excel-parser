package ru.siblion.excel.parser.excelparser.core;

import java.io.File;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExporterXML implements Exporter{

    private Document document;
    private Transformer transformer;

    public ExporterXML() throws Exception {
        try {
            DocumentBuilderFactory dbFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.newDocument();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
        } catch (ParserConfigurationException | TransformerException e) {
            throw new Exception( "Error while initialization ExporterXML");
        }
    }

    public void export(Content content) throws Exception{
        createXml(content);
        try {
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(System.getProperty("user.dir")+ File.separator+"config.xml"));
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new Exception( "Error while write xml file");
        }
    }

    private ModuleMethodContent getModuleMethodContent (Content content){
        return content instanceof ModuleMethodContent ? (ModuleMethodContent) content : null;
    }

    private void createXml (Content content)  {
        ModuleMethodContent moduleMethodContent = getModuleMethodContent(content);
        Element modules = createChildElement(null,"modules");
        for (String moduleName : moduleMethodContent.getContent().keySet()) {
            Element module = createChildElementWithAttribute(modules,"module","name",moduleName);
            Element classes = createChildElement(module,"classes");
            Map <String, List< String>> classWithMethods =  moduleMethodContent.getContent().get(moduleName);
            for (String className : classWithMethods.keySet()) {
                Element classElement = createChildElementWithAttribute(classes,"class","name",className);
                Element points = createChildElement(classElement,"points");
                for (String methodName : classWithMethods.get(className)) {
                    createChildElementWithAttribute(points,"point","name",methodName);
                }
            }
        }
    }

    private Element createChildElement(Element rootElement, String nameElement){

        Element childElement = document.createElement(nameElement);
        if(rootElement != null) {
            rootElement.appendChild(childElement);
        } else {
            document.appendChild(childElement);
        }
        return childElement;
    }


    private Element createChildElementWithAttribute(Element rootElement, String nameElement, String attributeName, String attributeValue) {

        Element childElement = createChildElement(rootElement, nameElement);
        Attr attribute = document.createAttribute(attributeName);
        attribute.setValue(attributeValue);
        childElement.setAttributeNode(attribute);
        return childElement;
    }

}
