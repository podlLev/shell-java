package command.builtin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RmdirBuiltin implements Builtin {

    public static final RmdirBuiltin INSTANCE = new RmdirBuiltin();

    @Override
    public String name() { return "rmdir"; }

    @Override
    public void execute(String command, List<String> args) {
        if (args.isEmpty()) {
            System.out.println("rmdir: missing operand");
            return;
        }

        for (String path : args) {
            File dir = new File(path);
            if (!dir.exists()) {
                System.out.printf("rmdir: %s: No such file or directory%n", path);
            } else if (!dir.isDirectory()) {
                System.out.printf("rmdir: %s: Not a directory%n", path);
            } else if (dir.list() != null && Objects.requireNonNull(dir.list()).length > 0) {
                System.out.printf("rmdir: %s: Directory not empty%n", path);
            } else if (!dir.delete()) {
                System.out.printf("rmdir: %s: Cannot remove directory%n", path);
            }
        }
    }

}
