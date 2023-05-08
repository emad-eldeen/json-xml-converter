package json.xml.converter.xml;

import json.xml.converter.json.Attribute;
import json.xml.converter.json.JsonElement;
import json.xml.converter.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

public class XmlElement {
    String tagName;
    List<XmlAttribute> attributes = new ArrayList<>();
    // in case the XML element has a string value
    String stringValue;
    XmlElement parentElement;
    List<XmlElement> children = new ArrayList<>();
    BodyType bodyType;

    public XmlElement() {}

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public List<XmlElement> getChildren() {
        return children;
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
        return stringValue != null || !children.isEmpty();
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

    public void addAttribute(XmlAttribute xmlAttribute) {
        attributes.add(xmlAttribute);
    }

    /**
     * @return the path to this xml element in the xml
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

    // get the attributes as a string separated by space
    String getAttributesRaw() {
        return attributes.stream()
                .map(XmlAttribute::toString)
                .collect(Collectors.joining(" ", " ", ""));
    }

    /**
     * @return the xml element, including all its children, as a string
     */
    @Override
    public String toString() {
        if (!hasChildren() && stringValue == null) {
            return "<%s%s/>".formatted(tagName, getAttributesRaw());
        } else  {
            // body is either children elements or value
            String body = hasChildren() ? getChildrenString() : stringValue;
            return "<%s%s>%s</%s>".formatted(tagName, getAttributesRaw(), body, tagName);
        }
    }

    String getPathString() {
        return getPath().stream()
                .map(XmlElement::getTagName)
                .collect(Collectors.joining(", "));
    }

    public String getValueString() {
        if (stringValue == null  || stringValue.length() == 0) {
            return "value = null";
        } else {
            return "value = \"%s\"".formatted(stringValue);
        }
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

    public void printObjectValueString() {
        children.forEach(XmlElement::printTree);
    }

    public void printTree() {
        System.out.printf("""
                        Element:
                        path = %s
                        %s
                        %s
                        """,
                getPathString(),
                getValueString(),
                getAttributesString());
        printObjectValueString();
    }
}


