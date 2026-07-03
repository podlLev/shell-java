package command.builtin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import redirect.OutputWriter;
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
            System.err.println("touch: missing operand");
            return;
        }

        for (String path : args) {
            File file = new File(path);
            try {
                if (file.exists()) {
                    if (!file.setLastModified(System.currentTimeMillis())) {
                        OutputWriter.writeError("touch: Cannot update timestamp", redirect);
                    }
                } else {
                    File parent = file.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }
                    if (!file.createNewFile()) {
                        OutputWriter.writeError("touch: Cannot create file", redirect);
                    }
                }
            } catch (IOException e) {
                OutputWriter.writeError(String.format("touch: %s: %s", path, e.getMessage()), redirect);
            }
        }
    }
}
