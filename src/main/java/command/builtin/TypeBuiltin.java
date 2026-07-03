package command.builtin;

import lombok.RequiredArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;
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
    public void execute(String command, List<String> args, Redirect redirect) {
        String argument = args.get(0);
        if (builtinNames.contains(argument)) {
            OutputWriter.write(argument + " is a shell builtin", redirect);
            return;
        }

        String path = PathResolver.find(argument, env);
        if (path != null) {
            OutputWriter.write(argument + " is " + path, redirect);
        } else {
            OutputWriter.write(argument + ": not found", redirect);
        }
    }

}
