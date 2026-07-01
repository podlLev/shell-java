package command.builtin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MkdirBuiltin implements Builtin {

    public static final MkdirBuiltin INSTANCE = new MkdirBuiltin();

    @Override
    public String name() { return "mkdir"; }

    @Override
    public void execute(String command, List<String> args) {
        if (args.isEmpty()) {
            System.out.println("mkdir: missing operand");
            return;
        }

        for (String path : args) {
            File dir = new File(path);
            if (dir.exists()) {
                System.out.printf("mkdir: %s: File exists%n", path);
            } else if (!dir.mkdirs()) {
                System.out.printf("mkdir: %s: Cannot create directory%n", path);
            }
        }
    }

}
