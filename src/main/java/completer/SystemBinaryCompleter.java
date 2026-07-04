package completer;

import lombok.RequiredArgsConstructor;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import util.Environment;

import java.io.File;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public final class SystemBinaryCompleter implements Completer {

    private static final Set<String> WINDOWS_EXECUTABLE_EXTENSIONS = Set.of(
            ".exe", ".bat", ".cmd", ".com"
    );

    private final Environment env;

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        if (line.wordIndex() != 0) return;

        String word = line.word().toLowerCase();
        String pathEnv = env.getPath();

        if (pathEnv == null || pathEnv.isBlank()) return;

        for (String dir : pathEnv.split(File.pathSeparator)) {
            File folder = new File(dir);
            if (!folder.isDirectory()) continue;

            File[] files = folder.listFiles();
            if (files == null) continue;

            for (File file : files) {
                if (!isExecutable(file)) continue;

                String name = stripExtension(file.getName());
                if (name.toLowerCase().startsWith(word)) {
                    candidates.add(new Candidate(name));
                }
            }
        }
    }

    private boolean isExecutable(File file) {
        if (!file.isFile()) return false;
        if (!env.isWindows()) return file.canExecute();

        String name = file.getName().toLowerCase();
        return WINDOWS_EXECUTABLE_EXTENSIONS.stream()
                .anyMatch(name::endsWith);
    }

    private String stripExtension(String filename) {
        if (env.isWindows() && filename.contains(".")) {
            return filename.substring(0, filename.lastIndexOf('.'));
        }
        return filename;
    }

}
