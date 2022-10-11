package tk.tommy;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public class Test {
    public static void main(String[] args) {

        URL resource = Test.class.getClassLoader().getResource("/conflict_names.csv");
        Path path = Path.of(resource.toString());
        System.out.println("path = " + path);
    }
}
