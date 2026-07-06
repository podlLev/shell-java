package command.builtin;

import lombok.RequiredArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;
import util.Environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public final class HistoryBuiltin implements Builtin {

    private final Environment env;

    @Override
    public String name() {
        return "history";
    }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        if (!args.isEmpty() && args.get(0).equals("-r")) {
            if (args.size() >= 2) handleRead(args.get(1), redirect);
            return;
        }

        List<String> entries = env.getHistoryManager().getEntries();
        int start = 0;
        if (!args.isEmpty()) {
            try {
                int n = Integer.parseInt(args.get(0));
                start = Math.max(0, entries.size() - n);
            } catch (NumberFormatException e) {
                System.err.printf("history: %s: numeric argument required%n", args.get(0));
                return;
            }
        }
        for (int i = start; i < entries.size(); i++) {
            OutputWriter.write(String.format("%5d  %s", i + 1, entries.get(i)), redirect);
        }
    }

    private void handleRead(String path, Redirect redirect) {
        try {
            List<String> lines = Files.readAllLines(Path.of(path));
            for (String line : lines) {
                if (!line.isBlank()) {
                    env.getHistoryManager().add(line);
                }
            }
        } catch (IOException e) {
            OutputWriter.writeError("history: " + path + ": " + e.getMessage(), redirect);
        }
    }

}
