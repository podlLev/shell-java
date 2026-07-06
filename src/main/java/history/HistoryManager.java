package history;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class HistoryManager {

    private static final String DEFAULT_HISTORY_FILE = ".shell_java_history";

    private final List<String> entries = new ArrayList<>();
    private final Path historyFile;

    public HistoryManager(String histFile) {
        this.historyFile = resolveHistoryFile(histFile);
    }

    public void add(String line) {
        if (entries.isEmpty() || !entries.get(entries.size() - 1).equals(line)) {
            entries.add(line);
        }
    }

    public List<String> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public void load() {
        load(historyFile);
    }

    public void load(Path path) {
        if (!Files.exists(path)) return;
        try (Stream<String> lines = Files.lines(path)) {
            lines.filter(line -> !line.isBlank())
                    .forEach(this::add);
        } catch (IOException e) {
            System.err.printf("warning: could not load history: %s%n", e.getMessage());
        }
    }

    public void save() {
        try {
            Files.write(historyFile, entries);
        } catch (IOException e) {
            System.err.printf("warning: could not save history: %s%n", e.getMessage());
        }
    }

    public void append(Path path) throws IOException {
        Files.write(path, entries, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public void writeTo(Path path) throws IOException {
        Files.write(path, entries);
    }

    private static Path resolveHistoryFile(String histFile) {
        if (histFile != null && !histFile.isBlank()) {
            return Path.of(histFile);
        }
        return Path.of(System.getProperty("user.home"), DEFAULT_HISTORY_FILE);
    }

}
