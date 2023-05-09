package json.xml.converter.json;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class JsonElement {
    String property;
    Object value;
    Json.ValueType valueType;
    JsonObject parentObject;
    List<Attribute> attributes = new ArrayList<>();

    JsonElement(String property, Object value, Json.ValueType valueType) {
        this.property = property;
        this.value = value;
        this.valueType = valueType;
    }

    public JsonElement() {
    }

    private String getValueForPrinting() {
        if (value == null) {
            return "null";
        } else if (value instanceof Integer) {
            return value.toString();
        } else if (value instanceof String) {
            return String.format("\"%s\"", value);
        } else if (value instanceof JsonObject) {
            return value.toString();
        } else {
            throw new RuntimeException("JsonElement value type not supported for printing");
        }
    }

    @Override
    public String toString() {
        return String.format("\"%s\":%s", property, getValueForPrinting());
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setValueType(Json.ValueType valueType) {
        this.valueType = valueType;
    }

    public String getProperty() {
        return property;
    }

    public Object getValue() {
        return value;
    }

    public Json.ValueType getValueType() {
        return valueType;
    }


    public void setParentObject(JsonObject parentElement) {
        this.parentObject = parentElement;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public boolean hasAttributes() {
        return attributes != null && attributes.size() > 0;
    }

    // get the path of this json element in the tree
    Deque<JsonElement> getPath() {
        Deque<JsonElement> path = new ArrayDeque<>();
        path.push(this);
        JsonObject jsonObject = parentObject;
        while (jsonObject != null && jsonObject.hasParent()) {
            path.push(jsonObject.parent);
            jsonObject = jsonObject.parent.parentObject;
        }
        return path;
    }

    // get the path as a string
    String getPathString() {
        return getPath().stream()
                .map(JsonElement::getProperty)
                .collect(Collectors.joining(", "));
    }

    public String getValueString() {
        if ((value == null || value instanceof JsonObject) ||
                (value instanceof String string && string.length() == 0)
        ) {
            return "value = null";
        } else {
            return "value = \"%s\"".formatted(value);
        }
    }

    private String getAttributesString() {
        StringBuilder builder = new StringBuilder();
        if (!attributes.isEmpty()) {
            builder.append("attributes:\n");
            for (Attribute attribute : attributes) {
                builder.append("%s = \"%s\"%n".formatted(
                        attribute.property(), attribute.value()));
            }
        }
        return builder.toString();
    }

    public void printObjectValueString() {
        if (value instanceof JsonObject jsonObject) {
            jsonObject.elements.forEach(JsonElement::printTree);
        }
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
