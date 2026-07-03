package command.builtin;

import lombok.RequiredArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;
import util.Environment;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public final class LsBuiltin implements Builtin {

    private final Environment env;

    @Override
    public String name() { return "ls"; }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        File dir = args.isEmpty()
                ? env.getCurrentDir()
                : new File(args.get(0));

        if (!dir.exists() || !dir.isDirectory()) {
            OutputWriter.writeError(String.format("ls: %s: No such directory", dir.getPath()), redirect);
            return;
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return;

        Arrays.sort(files, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        for (File file : files) {
            OutputWriter.write(file.getName(), redirect);
        }
    }

}
