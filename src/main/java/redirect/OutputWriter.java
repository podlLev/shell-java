package redirect;

import java.io.FileWriter;
import java.io.IOException;

public class OutputWriter {

    public static void write(String text, Redirect redirect) {
        if (redirect != null && redirect.type() == RedirectType.STDOUT) {
            writeToFile(text, redirect.filePath());
        } else {
            System.out.println(text);
        }
    }

    private static void writeToFile(String text, String path) {
        try (FileWriter fw = new FileWriter(path, false)) {
            fw.write(text + "\n");
        } catch (IOException e) {
            System.err.printf("Error writing to file: %s%n", e.getMessage());
        }
    }

}
