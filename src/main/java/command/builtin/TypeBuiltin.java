package command.builtin;

import lombok.RequiredArgsConstructor;
import util.Environment;
import util.PathResolver;

import java.util.Set;

@RequiredArgsConstructor
public final class TypeBuiltin implements Builtin {

    private final Set<String> builtinNames;
    private final Environment env;

    @Override
    public String name() {
        return "type";
    }

    @Override
    public void execute(String arg) {
        if (builtinNames.contains(arg)) {
            System.out.printf("%s is a shell builtin\n", arg);
            return;
        }

        String path = PathResolver.find(arg, env.getPath());
        if (path != null) {
            System.out.printf("%s is %s\n", arg, path);
        } else {
            System.out.printf("%s: not found\n", arg);
        }
    }

}
