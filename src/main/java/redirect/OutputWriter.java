package redirect;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OutputWriter {

    public static void write(String text, Redirect redirect) {
        if (redirect == null) {
            System.out.println(text);
            return;
        }
        switch (redirect.type()) {
            case STDOUT -> writeToFile(text, redirect.filePath(), false);
            default -> {
                System.out.println(text);
                createEmptyFile(redirect.filePath());
            }
        }
    }

    public static void writeError(String text, Redirect redirect) {
        if (redirect == null) {
            System.err.println(text);
            return;
        }
        switch (redirect.type()) {
            case STDERR -> writeToFile(text, redirect.filePath(), false);
            default -> {
                System.err.println(text);
                createEmptyFile(redirect.filePath());
            }
        }
    }

    private static void writeToFile(String text, String path, boolean append) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, append))) {
            bw.write(text);
            bw.newLine();
        } catch (IOException e) {
            System.err.printf("error writing to file: %s%n", e.getMessage());
        }
    }

    private static void createEmptyFile(String path) {
        try (FileWriter ignored = new FileWriter(path, false)) {
        } catch (IOException e) {
            System.err.printf("error creating file: %s%n", e.getMessage());
        }
    }

}
