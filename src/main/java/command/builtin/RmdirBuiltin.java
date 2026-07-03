package command.builtin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;

import java.io.File;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RmdirBuiltin implements Builtin {

    public static final RmdirBuiltin INSTANCE = new RmdirBuiltin();

    @Override
    public String name() { return "rmdir"; }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        if (args.isEmpty()) {
            System.err.println("rmdir: missing operand");
            return;
        }

        for (String path : args) {
            File dir = new File(path);
            if (!dir.exists()) {
                OutputWriter.writeError(String.format("rmdir: %s: No such file or directory", path), redirect);
            } else if (!dir.isDirectory()) {
                OutputWriter.writeError(String.format("rmdir: %s: Not a directory", path), redirect);
            } else if (dir.list() != null && Objects.requireNonNull(dir.list()).length > 0) {
                OutputWriter.writeError(String.format("rmdir: %s: Directory not empty", path), redirect);
            } else if (!dir.delete()) {
                OutputWriter.writeError(String.format("rmdir: %s: Cannot remove directory", path), redirect);
            }
        }
    }

}
