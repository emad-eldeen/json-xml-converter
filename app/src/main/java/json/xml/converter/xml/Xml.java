package json.xml.converter.xml;

import json.xml.converter.Element;
import java.util.ArrayList;
import java.util.List;

public class Xml extends Element {
    List<XmlElement> elements = new ArrayList<>();

    private static XmlElement.BodyType getElementBodyType(String body) {
        if (body.trim().charAt(0) == '<') {
            return XmlElement.BodyType.ELEMENTS;
        } else {
            return XmlElement.BodyType.STRING;
        }
    }

    public List<XmlElement> getElements() {
        return elements;
    }

    public void setElements(List<XmlElement> elements) {
        this.elements = elements;
    }


    public void addElement(XmlElement xmlElement) {
        elements.add(xmlElement);
    }

    @Override
    public void print() {
        for (XmlElement element : elements) {
            System.out.println(element);
        }
    }
}
