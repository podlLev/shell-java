package command.builtin;

import lombok.RequiredArgsConstructor;
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
    public void execute(String command, List<String> args) {
        File dir = args.isEmpty()
                ? env.getCurrentDir()
                : new File(args.get(0));

        if (!dir.exists() || !dir.isDirectory()) {
            System.out.printf("ls: %s: No such directory%n", dir.getPath());
            return;
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return;

        Arrays.sort(files, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        for (File file : files) {
            System.out.println(file.getName());
        }
    }

}
