package json.xml.converter.json;

import json.xml.converter.Element;
import json.xml.converter.Parser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonParser extends Parser {
    private static final Pattern JSON_OBJECT_PATTERN = Pattern.compile("\\{\\s*(?<objectElements>.+)\\s*}");
    private static final Pattern JSON_ElEMENTS_PATTERN = Pattern.compile(
            "\"(?<property>[@#]?[^\"]*)\"\\s*:\\s*(?<value>(?<STRING>\".*?\")|(?<OBJECT>\\{.*})|(?<NULL>null)|(?<NUMBER>\\d[.\\d]*)|(?<ARRAY>\\[.*]))\\s*");

    private static final Pattern VALUE_WITHOUT_QUOTES = Pattern.compile("\\s*\"(.+)\"");
    private static final Pattern ATTRIBUTE_WITHOUT_AT = Pattern.compile("\\s*@(.+)");
    private static final Pattern VALID_PROPERTY = Pattern.compile("[\\w.]+");

    /**
     * parses a json string and creates a Json Java object
     * @param jsonString the string to be parsed
     * @return the created json element
     */
    @Override
    public Element parse(String jsonString) {
        Json json = new Json();
        // assuming jsonObject
        json.setRootObject(parseJsonObject(jsonString));
        return json;
    }

    // parses a json object string into a json object
    private JsonObject parseJsonObject(String objectString) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.setElements(parseJsonElements(objectString, null));
        return jsonObject;
    }

    private Json.ValueType parseJsonElementValueType(Matcher matcher) {
        Json.ValueType matchedValueType = null;
        for (Json.ValueType valueType : Json.ValueType.values()) {
            String matchedString = matcher.group(valueType.name());
            if (matchedString != null) {
                matchedValueType = valueType;
            }
        }
        return matchedValueType;
    }

    // in case the value of the json element is primitive
    private void setJsonElementPrimitiveValue(JsonElement element, String objectValueInString) {
        switch (element.getValueType()) {
            case NULL -> element.setValue(null);
            case NUMBER -> element.setValue(Float.valueOf(objectValueInString));
            case STRING -> {
                String valueWithoutQuotes = objectValueInString.substring(
                        1, objectValueInString.length() - 1);
                element.setValue(valueWithoutQuotes);
            }
        }
    }

    /**
     * a recursive function to parse json elements from a string
     * @param string the string to be parsed
     * @param parent parent json object for the elements to be parsed
     * @return the list of the created json elements
     */
    public List<JsonElement> parseJsonElements(String string, JsonObject parent) {
        List<JsonElement> jsonElements = new ArrayList<>();
        Matcher matcher = JSON_OBJECT_PATTERN.matcher(string);
        if (matcher.find()) {
            String objectBody = matcher.group("objectElements");
            // parse each child element as a separate element
            Matcher jsonElementsMatcher = JSON_ElEMENTS_PATTERN.matcher(objectBody);
            int indexToSearchFrom = 0;
            while (jsonElementsMatcher.find(indexToSearchFrom)) {
                boolean isValidElement = false;
                String property = jsonElementsMatcher.group("property");
                Matcher propertyMatcher = VALID_PROPERTY.matcher(property);
                JsonElement childJsonElement = new JsonElement();
                if (propertyMatcher.find()) {
                    childJsonElement.setProperty(propertyMatcher.group());
                    isValidElement = true;
                    jsonElements.add(childJsonElement);
                    childJsonElement.setParentObject(parent);
                }
                // in case the value of this element is an object
                String objectValue = jsonElementsMatcher.group("OBJECT");
                if (objectValue != null) {
                    // get the closing bracket of this object
                    int objectBracketClosingIndex = getBracketClosingIndex(objectValue, '}', 0);
                    // if not valid element, dont continue parsing it
                    indexToSearchFrom = jsonElementsMatcher.start("OBJECT") + objectBracketClosingIndex;
                    if (!isValidElement)  {
                        continue;
                    }
                    objectValue = objectValue.substring(0, objectBracketClosingIndex + 1);
                    if (isBodyAttributesAndValue(objectValue, childJsonElement.getProperty())) {
                        parseJsonElementsAsAttributes(objectValue, childJsonElement);
                    } else {
                        childJsonElement.setValueType(Json.ValueType.OBJECT);
                        JsonObject childJsonObject = new JsonObject();
                        childJsonObject.setParent(childJsonElement);
                        childJsonElement.setValue(childJsonObject);
                        childJsonObject.setElements(parseJsonElements(objectValue, childJsonObject));
                    }
                } else {
                    indexToSearchFrom = jsonElementsMatcher.end();
                    // if not valid element, don't continue parsing it
                    if (!isValidElement) {
                        continue;
                    }
                    childJsonElement.setValueType(parseJsonElementValueType(jsonElementsMatcher));
                    setJsonElementPrimitiveValue(childJsonElement, jsonElementsMatcher.group("value"));
                }
            }
        }
        return jsonElements;
    }
    public void parseJsonElementsAsAttributes(String string, JsonElement parent) {
        Matcher matcher = JSON_OBJECT_PATTERN.matcher(string);
        if (matcher.find()) {
            String objectBody = matcher.group("objectElements");
            parseAttributesAndValue(objectBody, parent);
        }
    }

    boolean validAttribute(String property, String value) {
        if (property == null || property.length() == 0 || property.charAt(0) != '@' || property.length() < 2) {
            return false;
        }
        // value must be primitive
        return value == null || (value.charAt(0) != '{' && value.charAt(0) != '[');
    }

    boolean validAsValueForElementWithAttributes(String property, String parentPropertyName) {
        return property != null
                && property.length() > 0
                && property.charAt(0) == '#'
                && Objects.equals(parentPropertyName, property.substring(1));
    }

    void parseAttributesAndValue(String objectString, JsonElement parent) {
        Map<String, String> elementsPropertyValue = getElementsPropertyValue(objectString);
        elementsPropertyValue.forEach((key, value) -> {
            if (validAttribute(key, value)) {
                // add the attribute after removing the first char @ from the key
                // and the " from the value
                parent.addAttribute(new Attribute(getAttributeWithoutAt(key), getValueWithoutSurroundingQuotes(value)));
            } else if (validAsValueForElementWithAttributes(key, parent.getProperty())) {
                // if value is object
                if (value.matches("\\s*\\{.+")) {
                    JsonObject jsonObject = parseJsonObject(value);
                    jsonObject.setParent(parent);
                    parent.setValueType(Json.ValueType.OBJECT);
                    parent.setValue(jsonObject);
                } else {
                    String strippedValue = getValueWithoutSurroundingQuotes(value);
                    if (strippedValue.matches("\\d.*")) {
                        parent.setValueType(Json.ValueType.NUMBER);
                    } else if ("null".equals(strippedValue)) {
                        parent.setValueType(Json.ValueType.NULL);
                    } else {
                        parent.setValueType(Json.ValueType.STRING);
                    }
                    parent.setValue(strippedValue);
                }
            }
            // else discard the element
        });
    }

    boolean isBodyAttributesAndValue(String bodyString, String parentPropertyName) {
        if (parentPropertyName == null) {
            return false;
        }

        Map<String, String> elementsString = getElementsPropertyValue(bodyString);
        boolean hasNoneValidValueElement = elementsString.entrySet().stream()
                .noneMatch(mapEntry -> validAsValueForElementWithAttributes(
                                mapEntry.getKey(),
                                parentPropertyName
                        )
                );
        if (hasNoneValidValueElement) {
            return false;
        }
        long validAttributesCount = elementsString.entrySet().stream().filter(mapEntry -> validAttribute(
                mapEntry.getKey(), mapEntry.getValue())).count();
        // no attributes or all valid
        return elementsString.size() == 1 || validAttributesCount == elementsString.size() - 1;
    }

    private static Map<String, String> getElementsPropertyValue(String bodyString) {
        Matcher matcher = JSON_ElEMENTS_PATTERN.matcher(bodyString);
        // a map of property/value
        Map<String, String> elementsString = new HashMap<>();
        while (matcher.find()) {
            elementsString.put(
                    matcher.group("property"),
                    matcher.group("value")
            );
        }
        return elementsString;
    }

    int getBracketClosingIndex(String string, char closingBracket, int searchFromIndex) {
        int openingBracketIndex = string.indexOf('{', searchFromIndex);
        // starts from after the opening bracket
        int regexSearchIndex = openingBracketIndex + 1;
        // TODO handle square brackets too
        int firstClosingBracketIndex = string.indexOf(closingBracket, regexSearchIndex);
        // if string has unescaped bracket
        Matcher matcher = Pattern.compile(".*?[^\\\\]?(\\{.*)").matcher(string);
        while (matcher.find(regexSearchIndex)) {
            int nextOpeningBracketStartIndex = matcher.start(1);
            if (firstClosingBracketIndex < nextOpeningBracketStartIndex) {
                // it means that this object is closed before opening the new object
                return firstClosingBracketIndex;
            }
            // it means that this is an inner object
            int innerBracketClosingIndex = getBracketClosingIndex(string, '}', nextOpeningBracketStartIndex);
            // start searching again from the part after closing the bracket
            regexSearchIndex = innerBracketClosingIndex + 1;
            firstClosingBracketIndex = string.indexOf(closingBracket, regexSearchIndex);
        }
        return firstClosingBracketIndex;
    }

    String getValueWithoutSurroundingQuotes(String value) {
        Matcher matcher = VALUE_WITHOUT_QUOTES.matcher(value);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return value;
    }

    String getAttributeWithoutAt(String value) {
        Matcher matcher = ATTRIBUTE_WITHOUT_AT.matcher(value);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return value;
    }

}
