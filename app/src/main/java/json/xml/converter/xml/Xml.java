package json.xml.converter.xml;

import json.xml.converter.Element;
import json.xml.converter.json.Json;
import json.xml.converter.json.JsonElement;
import json.xml.converter.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Xml extends Element {
    List<XmlElement> elements = new ArrayList<>();

    /**
     * converts a Json object to an xml
     * @param object json object to be converted
     * @return the corresponding xml object
     */
    public static Xml createXml(JsonObject object) {
        Xml xml = new Xml();
        List<XmlElement> elements = new ArrayList<>();
        for (JsonElement jsonElement : object.getElements()) {
            elements.add(createXmlElementFromJson(jsonElement, null));
        }
        if (elements.size() > 1) {
            // add them to a root element
            XmlElement xmlElement = new XmlElement();
            xmlElement.setTagName("root");
            xmlElement.setChildren(elements);
            xml.setElements(List.of(xmlElement));
        } else {
            xml.setElements(elements);
        }
        return xml;
    }

    private static XmlElement createXmlElementFromJson(JsonElement jsonElement, XmlElement parent) {
        XmlElement xmlElement = new XmlElement();
        xmlElement.setParentElement(parent);
        xmlElement.setTagName(jsonElement.getProperty());
        xmlElement.setBodyType(jsonElementValueTypeToXmlElementBodyType(jsonElement.getValueType()));
        switch (xmlElement.bodyType) {
            case NULL -> xmlElement.setStringValue(null);
            case STRING -> xmlElement.setStringValue(jsonElement.getValue().toString());
            case ELEMENTS -> {
                JsonObject inner = (JsonObject) jsonElement.getValue();
                List<XmlElement> children = new ArrayList<>();
                inner.getElements().forEach(element -> children.add(createXmlElementFromJson(element, xmlElement)));
                xmlElement.setChildren(children);
            }
        }
        if (jsonElement.hasAttributes()) {
            jsonElement.getAttributes().forEach(attribute -> xmlElement.addAttribute(
                    new XmlAttribute(attribute.property(), attribute.value())
            ));
        }
        return xmlElement;
    }

    // get xml element body type based on string
    private static XmlElement.BodyType getElementBodyType(String body) {
        if (body.trim().charAt(0) == '<') {
            return XmlElement.BodyType.ELEMENTS;
        } else {
            return XmlElement.BodyType.STRING;
        }
    }

    // converts between Json element type and xml element type
    private static XmlElement.BodyType jsonElementValueTypeToXmlElementBodyType(Json.ValueType valueType) {
        if (valueType == null) {
            return XmlElement.BodyType.NULL;
        }
        switch (valueType) {
            case ARRAY, OBJECT -> {
                return XmlElement.BodyType.ELEMENTS;
            }
            case STRING, NUMBER -> {
                return XmlElement.BodyType.STRING;
            }
            default -> {
                return XmlElement.BodyType.NULL;
            }
        }
    }

    public List<XmlElement> getElements() {
        return elements;
    }

    public void setElements(List<XmlElement> elements) {
        this.elements = elements;
    }

    private static String getXmlElementBodyFromJsonObject(JsonObject jsonObject) {
        String body = null;
        Optional<JsonElement> elementWithBody = jsonObject.getElements().stream()
                .filter(element -> element.getProperty().charAt(0) == '#')
                .findFirst();
        if (elementWithBody.isPresent()) {
            if (elementWithBody.get().getValue() != null) {
                body = String.valueOf(elementWithBody.get().getValue());
            }
        }
        return body;
    }

    public void addElement(XmlElement xmlElement) {
        elements.add(xmlElement);
    }

    @Override
    public void printTree() {
        for (XmlElement element : elements) {
            element.printTree();
        }
        System.out.println();
    }

    @Override
    public Element convert() {
        return toJson();
    }


    Json toJson() {
        Json json = new Json();
        for (XmlElement xmlElement : elements) {
            json.getRootObject().addElement(Json.createJsonElement(xmlElement));
        }
        return json;
    }

    @Override
    public void printRaw() {
        for (XmlElement element : elements) {
            System.out.print(element);
        }
        System.out.println();
    }
}