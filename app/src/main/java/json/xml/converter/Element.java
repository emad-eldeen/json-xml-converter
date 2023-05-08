package json.xml.converter;

public abstract class Element {
    public abstract void printTree();
    public abstract void printRaw();
    public abstract Element convert();
}
