package command.builtin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import redirect.Redirect;

import java.io.File;
import java.io.IOException;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TouchBuiltin implements Builtin {

    public static final TouchBuiltin INSTANCE = new TouchBuiltin();

    @Override
    public String name() { return "touch"; }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        if (args.isEmpty()) {
            System.out.println("touch: missing operand");
            return;
        }

        for (String path : args) {
            File file = new File(path);
            try {
                if (file.exists()) {
                    if (!file.setLastModified(System.currentTimeMillis())) {
                        System.out.printf("touch: %s: Cannot update timestamp%n", path);
                    }
                } else {
                    File parent = file.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }
                    if (!file.createNewFile()) {
                        System.out.printf("touch: %s: Cannot create file%n", path);
                    }
                }
            } catch (IOException e) {
                System.out.printf("touch: %s: %s%n", path, e.getMessage());
            }
        }
    }
}
