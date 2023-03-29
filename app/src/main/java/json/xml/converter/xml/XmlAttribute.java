package json.xml.converter.xml;

public record XmlAttribute(String property, String value) {
    @Override
    public String toString() {
        return "%s=\"%s\"".formatted(property, value);
    }
}
