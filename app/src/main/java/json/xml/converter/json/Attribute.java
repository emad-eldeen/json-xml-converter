package json.xml.converter.json;

public record Attribute(String property, String value) {
    @Override
    public String toString() {
        return "%s=\"%s\"".formatted(property, value);
    }
}
