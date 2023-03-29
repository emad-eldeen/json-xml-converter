package json.xml.converter.xml;


import json.xml.converter.Element;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class XmlElement extends Element {
    String tagName;
    List<XmlAttribute> attributes = new ArrayList<>();
    String value;
    XmlElement parentElement;
    List<XmlElement> children = new ArrayList<>();
    BodyType bodyType;

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setChildren(List<XmlElement> children) {
        this.children = children;
    }

    public enum BodyType {
        STRING,
        ELEMENTS,
        NULL
    }

    public XmlElement(String tagName, List<XmlAttribute> attributes) {
        this.tagName = tagName;
        this.attributes = attributes;
    }


    public XmlElement() {
    }

    public String getTagName() {
        return tagName;
    }

    public List<XmlAttribute> getAttributes() {
        return attributes;
    }

    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    public boolean hasBody() {
        return value != null || !children.isEmpty();
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public void setAttributes(List<XmlAttribute> attributes) {
        this.attributes = attributes;
    }

    public void setParentElement(XmlElement parentElement) {
        this.parentElement = parentElement;
    }

    public boolean hasParent() {
        return parentElement != null;
    }

    /*
    @Override
    public String toString() {
        String attributes = getAttributes().stream()
                .map(XmlAttribute::toString)
                .collect(Collectors.joining(" "));
        if (hasBody()) {
            return "<%s %s>%s</%s>".formatted(
                    tagName, attributes, body, tagName);
        }
        return "<%s %s/>".formatted(tagName, attributes);
    }
    */


    private Deque<XmlElement> getPath() {
        Deque<XmlElement> path = new ArrayDeque<>();
        XmlElement xmlElement = this;
        path.push(xmlElement);
        while (xmlElement.hasParent()) {
            xmlElement = xmlElement.parentElement;
            path.push(xmlElement);
        }
        return path;
    }

    private String getAttributesString() {
        StringBuilder builder = new StringBuilder();
        if (!attributes.isEmpty()) {
            builder.append("attributes:\n");
            for (XmlAttribute attribute : attributes) {
                builder.append("%s = \"%s\"%n".formatted(
                        attribute.property(), attribute.value()));
            }
        }
        return builder.toString();
    }

    public String getChildrenString() {
        if (!hasChildren()) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            for (XmlElement element : children) {
                builder.append(element);
            }
            return builder.toString();
        }
    }

    @Override
    public void print() {

    }

    @Override
    public String toString() {
        return """
                Element:
                path = %s
                value = %s
                %s
                %s%n
                """.formatted(
                getPath().stream()
                        .map(XmlElement::getTagName)
                        .collect(Collectors.joining(", ")),
                value == null ? "null" : value,
                getAttributesString(),
                getChildrenString());
    }
}
