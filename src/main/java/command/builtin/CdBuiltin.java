package command.builtin;

import lombok.RequiredArgsConstructor;
import util.Environment;

import java.io.File;
import java.util.List;

@RequiredArgsConstructor
public final class CdBuiltin implements Builtin {

    private final Environment env;

    @Override
    public String name() {
        return "cd";
    }

    @Override
    public void execute(String command, List<String> args) {
        String path = args.get(0);
        File target = resolve(path);

        try {
            target = target.getCanonicalFile();
        } catch (Exception e) {
            printError(path);
            return;
        }

        if (!target.exists() || !target.isDirectory()) {
            printError(path);
            return;
        }
        env.setCurrentDir(target);
    }

    private File resolve(String path) {
        if (path.equals("~")) return new File(env.getHome());
        if (path.startsWith("/")) return new File(path);
        return new File(env.getCurrentDir(), path);
    }

    private void printError(String path) {
        System.out.printf("cd: %s: No such file or directory\n", path);
    }

}
