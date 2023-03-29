package json.xml.converter;

import json.xml.converter.xml.XmlParser;

class ParserFactory {
    static Parser createParser() {
        return new XmlParser();
    }
}
