package command.builtin;

import lombok.RequiredArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;
import util.Environment;

import java.io.IOException;
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
        if (!args.isEmpty()) {
            switch (args.get(0)) {
                case "-r" -> {
                    if (args.size() >= 2) {
                        env.getHistoryManager().load(Path.of(args.get(1)));
                    }
                }
                case "-w" -> {
                    if (args.size() >= 2) writeHistory(args.get(1), false, redirect);
                    return;
                }
                case "-a" -> {
                    if (args.size() >= 2) writeHistory(args.get(1), true, redirect);
                    return;
                }
            }
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

    private void writeHistory(String path, boolean append, Redirect redirect) {
        try {
            if (append) {
                env.getHistoryManager().append(Path.of(path));
            } else {
                env.getHistoryManager().writeTo(Path.of(path));
            }
        } catch (IOException e) {
            OutputWriter.writeError("history: " + e.getMessage(), redirect);
        }
    }

}
