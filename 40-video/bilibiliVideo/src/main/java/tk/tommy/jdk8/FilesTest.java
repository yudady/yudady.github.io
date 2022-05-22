package tk.tommy.jdk8;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FilesTest {
    public static void main(String[] args) throws IOException {
        System.out.println("\nFiles.list");
        Files.list(Paths.get("."))
            .filter(path -> !path.toString().endsWith(".iml"))
            .forEach(path -> System.out.println(path));

        System.out.println("\nFiles.walk");
        Files.walk(Paths.get("."), 1, FileVisitOption.values())
            //.filter(path -> path.toString().startsWith(".\\src"))
            .forEach(path -> System.out.println(path));

        System.out.println("\nFiles.walk 2");
        Files.walk(Paths.get("."), FileVisitOption.values())
            .filter(path -> path.toString().endsWith(".java"))
            .forEach(path -> System.out.println(path));
    }
}
