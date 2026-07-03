package command.builtin;

import lombok.RequiredArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;
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
    public void execute(String command, List<String> args, Redirect redirect) {
        String path = args.get(0);
        File target = resolve(path);

        try {
            target = target.getCanonicalFile();
        } catch (Exception e) {
            printError(path, redirect);
            return;
        }

        if (!target.exists() || !target.isDirectory()) {
            printError(path, redirect);
            return;
        }
        env.setCurrentDir(target);
    }

    private File resolve(String path) {
        if (path.equals("~")) return new File(env.getHome());
        if (path.startsWith("/")) return new File(path);
        return new File(env.getCurrentDir(), path);
    }

    private void printError(String path, Redirect redirect) {
        OutputWriter.writeError("cd: " + path + ": No such file or directory", redirect);
    }

}
