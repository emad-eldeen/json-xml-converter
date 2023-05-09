package json.xml.converter.json;

import json.xml.converter.Element;
import json.xml.converter.xml.Xml;
import json.xml.converter.xml.XmlAttribute;
import json.xml.converter.xml.XmlElement;

public class Json extends Element {
    JsonObject rootObject;
    public enum ValueType {
        ARRAY,
        NULL,
        NUMBER,
        OBJECT,
        STRING
    }

    public Json() {
        this.rootObject = new JsonObject();
    }

    public void setRootObject(JsonObject rootObject) {
        this.rootObject = rootObject;
    }

    public JsonObject getRootObject() {
        return rootObject;
    }

    /**
     * converts an Xml element to a Json element
     * @param xmlElement the xml element to be converted
     * @return the created Json element
     */
    public static JsonElement createJsonElement(XmlElement xmlElement) {
        JsonElement jsonElement = new JsonElement();
        jsonElement.property = xmlElement.getTagName();
        if (xmlElement.hasAttributes()) {
            JsonObject childObject = new JsonObject();
            jsonElement.value = childObject;
            for (XmlAttribute attribute: xmlElement.getAttributes()) {
                String property = "@" + attribute.property();
                String value = attribute.value();
                childObject.addElement(new JsonElement(property, value, ValueType.STRING));
            }
            // add content as property under #tagname
            JsonElement elementForXmlElementBody = new JsonElement();
            childObject.addElement(elementForXmlElementBody);
            elementForXmlElementBody.property = "#" + xmlElement.getTagName();
            // depending on body type
            setJsonElementValueBasedOnXmlBodyType(xmlElement, elementForXmlElementBody);
        } else {
            // assuming string
            setJsonElementValueBasedOnXmlBodyType(xmlElement, jsonElement);
        }
        return jsonElement;
    }


    // converts Xml body types to Json body types
    static void setJsonElementValueBasedOnXmlBodyType(XmlElement xmlElement, JsonElement jsonElement) {
        if (xmlElement.getBodyType() == null) {
            jsonElement.setValueType(ValueType.NULL);
            jsonElement.setValue(null);
            return;
        }
        switch (xmlElement.getBodyType()) {
            case STRING -> {
                jsonElement.setValueType(ValueType.STRING);
                jsonElement.setValue(xmlElement.getStringValue());
            }
            case ELEMENTS -> {
                jsonElement.setValueType(ValueType.OBJECT);
                JsonObject jsonObject = new JsonObject();
                xmlElement.getChildren().forEach(child -> jsonObject.addElement(createJsonElement(child)));
                jsonElement.setValue(jsonObject);
            }
            default -> {
                jsonElement.setValueType(ValueType.NULL);
                jsonElement.setValue(null);
            }
        }
    }

    @Override
    public Element convert() {
        return toXml();
    }
    public void printTree() {
        rootObject.printTree();
    }

    @Override
    public void printRaw() {
        System.out.println(rootObject);
    }

    Xml toXml() {
        return Xml.createXml(rootObject);
    }
}
