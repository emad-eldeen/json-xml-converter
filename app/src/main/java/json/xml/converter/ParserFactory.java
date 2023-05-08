package json.xml.converter;

import json.xml.converter.json.JsonParser;
import json.xml.converter.xml.XmlParser;

class ParserFactory {
    static Parser createParser(String userInput) {
        if (userInput.trim().charAt(0) == '<') {
            return new XmlParser();
        } else {
            return new JsonParser();
        }
    }
}