package json.xml.converter.xml;

import json.xml.converter.Element;
import json.xml.converter.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlParser extends Parser {
    private final Pattern XML_NON_EMPTY_ELEMENT_PATTERN = Pattern.compile("<(?<tagName>\\w+)(?<attributes>.*?)>(?<body>.*)</\\w+>$") ;
    private final Pattern XML_EMPTY_ELEMENT_PATTERN= Pattern.compile("<(?<tagName>\\w+)(?<attributes>[^>]*?)(/>)$") ;
    private final Pattern XML_ELEMENTS_PATTERN = Pattern.compile("<(?<tagName>\\w+)(([^<]*/>)|(.*?/(\\k<tagName>)>))") ;
    private final Pattern ATTRIBUTE_PATTERN = Pattern.compile("(\\S+)\\s*=\\s*\"(.+?)\"") ;

    /**
     * parses an XML element from a string
     * @param string the string to be parsed into xml
     * @return the xml element
     */
    @Override
    public Element parse(String string) {
        Xml xml = new Xml();
        xml.addElement(parseXmlElement(string, null));
        return xml;
    }

    // parses a string into a list of xml attributes
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

    /**
     * a recursive function that parses an xml element from string
     * @param xmlElementString xml string
     * @param parentElement the parent of the element to be parsed. null if root element
     * @return the parsed xml
     */
    private XmlElement parseXmlElement(String xmlElementString, XmlElement parentElement) {
        XmlElement xmlElement = new XmlElement();
        xmlElement.setParentElement(parentElement);
        Matcher emptyElementMatcher = XML_EMPTY_ELEMENT_PATTERN.matcher(xmlElementString);
        // the part of the xml element holding the attributes
        String attributesString;
        // in case of an empty xml element
        if (emptyElementMatcher.find()) {
            xmlElement.setTagName(emptyElementMatcher.group("tagName"));
            xmlElement.setStringValue(null);
            attributesString = emptyElementMatcher.group("attributes");
        } else {
            // non-empty element matcher
            Matcher nonEmptyElementMatcher = XML_NON_EMPTY_ELEMENT_PATTERN.matcher(xmlElementString);
            if (nonEmptyElementMatcher.find()) {
                xmlElement.setTagName(nonEmptyElementMatcher.group("tagName"));
                attributesString = nonEmptyElementMatcher.group("attributes");
                String bodyString = nonEmptyElementMatcher.group("body");
                xmlElement.setBodyType(parseBodyType(bodyString));
                // in case the body of the xml element is just a string value
                if (xmlElement.getBodyType() == XmlElement.BodyType.STRING) {
                    xmlElement.setStringValue(bodyString);
                } else {
                    // the body of the element is an object that needs to be parsed
                    xmlElement.setChildren(parseXmlElements(bodyString, xmlElement));
                }
            } else {
                throw new IllegalArgumentException("Invalid XML");
            }
        }
        xmlElement.setAttributes(parseAttributes(attributesString));
        return xmlElement;
    }

    // get the type of the body of the xml element
    XmlElement.BodyType parseBodyType(String bodyString) {
        return (bodyString.trim().length() > 0 && bodyString.trim().charAt(0) == '<') ?
                XmlElement.BodyType.ELEMENTS :
                XmlElement.BodyType.STRING;
    }

    /**
     * parses the list of xml elements from a string
     * @param xmlElementsInString the string of the xml elements
     * @param parentElement
     * @return the list of xml elements
     */
    List<XmlElement> parseXmlElements(String xmlElementsInString, XmlElement parentElement) {
        List<XmlElement> xmlElements = new ArrayList<>();
        Matcher matcher = XML_ELEMENTS_PATTERN.matcher(xmlElementsInString);
        while (matcher.find()) {
            xmlElements.add(parseXmlElement(matcher.group(), parentElement));
        }
        return xmlElements;
    }
}
