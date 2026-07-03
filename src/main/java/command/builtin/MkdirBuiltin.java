package command.builtin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;

import java.io.File;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MkdirBuiltin implements Builtin {

    public static final MkdirBuiltin INSTANCE = new MkdirBuiltin();

    @Override
    public String name() { return "mkdir"; }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        if (args.isEmpty()) {
            System.err.println("mkdir: missing operand");
            return;
        }

        for (String path : args) {
            File dir = new File(path);
            if (dir.exists()) {
                OutputWriter.writeError(String.format("mkdir: %s: File exists", path), redirect);
            } else if (!dir.mkdirs()) {
                OutputWriter.writeError(String.format("mkdir: %s: Cannot create directory", path), redirect);
            }
        }
    }

}
