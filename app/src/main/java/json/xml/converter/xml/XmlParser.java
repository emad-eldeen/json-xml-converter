package json.xml.converter.xml;

import json.xml.converter.Element;
import json.xml.converter.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlParser extends Parser {
    private final Pattern XML_NON_EMPTY_ELEMENT_PATTERN = Pattern.compile("<(?<tagName>\\w+)(?<attributes>.*?)>(?<body>.*)</\\w+>$") ;
    private final Pattern XML_EMPTY_ELEMENT_PATTERN = Pattern.compile("<(?<tagName>\\w+)(?<attributes>[^>]*?)(([^<]*/>)|(></(\\k<tagName>)>))$") ;
    private final Pattern XML_ELEMENTS_PATTERN = Pattern.compile("<(?<tagName>\\w+)(([^<]*/>)|(.*?/(\\k<tagName>)>))") ;
    private final Pattern ATTRIBUTE_PATTERN = Pattern.compile("(\\S+)\\s*=\\s*\"(.+?)\"") ;
    @Override
    public Element parse(String string) {
        Xml xml = new Xml();
        xml.addElement(parseXmlElement(string, null));
        return xml;
    }
    private List<XmlAttribute> parseAttributes(String attributesString) {
        List<XmlAttribute> attributes = new ArrayList<>();
        Matcher matcher = ATTRIBUTE_PATTERN.matcher(attributesString);
        while (matcher.find()) {
            String attributeName = matcher.group(1);
            String attributeValue = matcher.group(2);
            attributes.add(new XmlAttribute(attributeName, attributeValue));
        }
        return attributes;
    }
    private XmlElement parseXmlElement(String xmlElementString, XmlElement parentElement) {
        XmlElement xmlElement = new XmlElement();
        xmlElement.setParentElement(parentElement);
        Matcher emptyElementMatcher = XML_EMPTY_ELEMENT_PATTERN.matcher(xmlElementString);
        String attributesString;
        if (emptyElementMatcher.find()) {
            xmlElement.setTagName(emptyElementMatcher.group("tagName"));
            xmlElement.setValue(null);
            attributesString = emptyElementMatcher.group("attributes");
        } else {
            Matcher nonEmptyElementMatcher = XML_NON_EMPTY_ELEMENT_PATTERN.matcher(xmlElementString);
            if (nonEmptyElementMatcher.find()) {
                xmlElement.setTagName(nonEmptyElementMatcher.group("tagName"));
                attributesString = nonEmptyElementMatcher.group("attributes");
                String bodyString = nonEmptyElementMatcher.group("body");
                xmlElement.setBodyType(parseBodyType(bodyString));
                if (xmlElement.getBodyType() == XmlElement.BodyType.STRING) {
                    xmlElement.setValue(bodyString);
                } else {
                    xmlElement.setChildren(parseXmlElements(bodyString, xmlElement));
                }
            } else {
                throw new IllegalArgumentException("Invalid XML");
            }
        }
        xmlElement.setAttributes(parseAttributes(attributesString));
        return xmlElement;
    }

    XmlElement.BodyType parseBodyType(String bodyString) {
        return bodyString.trim().charAt(0) == '<' ?
                XmlElement.BodyType.ELEMENTS :
                XmlElement.BodyType.STRING;
    }

    List<XmlElement> parseXmlElements(String xmlElementsInString, XmlElement parentElement) {
        List<XmlElement> xmlElements = new ArrayList<>();
        Matcher matcher = XML_ELEMENTS_PATTERN.matcher(xmlElementsInString);
        while (matcher.find()) {
            xmlElements.add(parseXmlElement(matcher.group(), parentElement));
        }
        return xmlElements;
    }
}
