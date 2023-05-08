package json.xml.converter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        System.out.println("""
                   $$$$$\\  $$$$$$\\   $$$$$$\\  $$\\   $$\\          $$\\     $$\\          $$\\   $$\\ $$\\      $$\\ $$\\      \s
                   \\__$$ |$$  __$$\\ $$  __$$\\ $$$\\  $$ |        $$  |    \\$$\\         $$ |  $$ |$$$\\    $$$ |$$ |     \s
                      $$ |$$ /  \\__|$$ /  $$ |$$$$\\ $$ |       $$  /      \\$$\\        \\$$\\ $$  |$$$$\\  $$$$ |$$ |     \s
                      $$ |\\$$$$$$\\  $$ |  $$ |$$ $$\\$$ |      $$  /$$$$$$\\ \\$$\\        \\$$$$  / $$\\$$\\$$ $$ |$$ |     \s
                $$\\   $$ | \\____$$\\ $$ |  $$ |$$ \\$$$$ |      \\$$< \\______|$$  |       $$  $$<  $$ \\$$$  $$ |$$ |     \s
                $$ |  $$ |$$\\   $$ |$$ |  $$ |$$ |\\$$$ |       \\$$\\       $$  /       $$  /\\$$\\ $$ |\\$  /$$ |$$ |     \s
                \\$$$$$$  |\\$$$$$$  | $$$$$$  |$$ | \\$$ |        \\$$\\     $$  /        $$ /  $$ |$$ | \\_/ $$ |$$$$$$$$\\\s
                 \\______/  \\______/  \\______/ \\__|  \\__|         \\__|    \\__/         \\__|  \\__|\\__|     \\__|\\________|
                                                                                                                      \s
                """);
        boolean exit = false;
        Scanner scanner = new Scanner(System.in);
        while (!exit) {
            System.out.println("Type the name of the file to be parsed, or 'exit' to exit the program");
            String userInput = scanner.nextLine();
            if ("exit".equals(userInput)) {
                exit = true;
            } else {
                try {
                    Path path = getFilePathFromResource(userInput);
                    // replace all new lines with spaces (make file as one line)
                    String fileString = String.join(" ", Files.readAllLines(path));
                    Parser parser = ParserFactory.createParser(fileString);
                    Element element = parser.parse(fileString);
                    System.out.println("Parsing complete!");
                    System.out.println("Available commands: 'print', 'print tree' and 'convert'");
                    userInput = scanner.nextLine();
                    switch (userInput) {
                        case "print" -> element.printRaw();
                        case "print tree" -> element.printTree();
                        case "convert" -> element.convert().printRaw();
                        default -> System.out.println("Invalid command!");
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        System.out.println("Goodbye!");
    }

    // get a file from the resources folder
    private static Path getFilePathFromResource(String fileName) {
        try {
            // The class loader that loaded the class
            ClassLoader classLoader = App.class.getClassLoader();
            return Path.of(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("file not found! " + fileName);
        }
    }
}
