package json.xml.converter.json;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonObject {
    List<JsonElement> elements = new ArrayList<>();
    JsonElement parent;

    public void setParent(JsonElement parent) {
        this.parent = parent;
    }

    public List<JsonElement> getElements() {
        return elements;
    }

    /**
     * adds a json element to the json object. in case the object already has an element with the same property name,
     * it will be replaced
     * @param element the element to be added
     */
    public void addElement(JsonElement element) {
        int indexOfElementWithSameProperty = -1;
        // check if there is an element with the same property name (Json object must have unique property names)
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).property.equals(element.property)) {
                indexOfElementWithSameProperty = i;
                break;
            }
        }
        if (indexOfElementWithSameProperty == -1) {
            elements.add(element);
        } else {
            // replace
            elements.set(indexOfElementWithSameProperty, element);
        }
    }

    public void setElements(List<JsonElement> elements) {
        this.elements = new ArrayList<>();
        elements.forEach(this::addElement);
    }

    public boolean hasParent() {
        return parent != null;
    }
    @Override
    public String toString() {
        String body = elements.stream()
                .map(JsonElement::toString)
                .collect(Collectors.joining(","));
        return "{%s}".formatted(body);
    }

    public void printTree() {
        elements.forEach(JsonElement::printTree);
    }
}
