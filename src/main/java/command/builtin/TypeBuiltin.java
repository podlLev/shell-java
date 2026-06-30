package command.builtin;

import lombok.RequiredArgsConstructor;
import util.Environment;
import util.PathResolver;

import java.util.List;
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
    public void execute(String command, List<String> args) {
        String argument = args.get(0);
        if (builtinNames.contains(argument)) {
            System.out.printf("%s is a shell builtin\n", argument);
            return;
        }

        String path = PathResolver.find(argument, env);
        if (path != null) {
            System.out.printf("%s is %s\n", argument, path);
        } else {
            System.out.printf("%s: not found\n", argument);
        }
    }

}
