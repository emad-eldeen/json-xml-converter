package json.xml.converter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) {
        try {
            Path path = getFilePathFromResource("input.txt");
            String fileString = String.join(" ", Files.readAllLines(path));
            System.out.println(fileString);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // get a file from the resources folder
    // works everywhere, IDEA, unit test and JAR file.
    private static Path getFilePathFromResource(String fileName) {
        try {
            // The class loader that loaded the class
            ClassLoader classLoader = App.class.getClassLoader();
            return Path.of(classLoader.getResource(fileName).toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("file not found! " + fileName);
        }
    }
}
