package command.builtin;

import lombok.RequiredArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;
import util.Environment;

import java.util.List;

@RequiredArgsConstructor
public final class AliasBuiltin implements Builtin {

    private final Environment env;

    @Override
    public String name() {
        return "alias";
    }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        if (args.isEmpty()) {
            env.getAliasManager().getAll().forEach((name, value) ->
                    OutputWriter.write(String.format("alias %s='%s'", name, value), redirect));
            return;
        }

        for (String arg : args) {
            if (arg.contains("=")) {
                int eq = arg.indexOf('=');
                String name = arg.substring(0, eq);
                String value = arg.substring(eq + 1)
                        .replaceAll("^'|'$", "")
                        .replaceAll("^\"|\"$", "");
                env.getAliasManager().set(name, value);
            } else {
                env.getAliasManager().get(arg).ifPresentOrElse(
                        value -> OutputWriter.write(
                                String.format("alias %s='%s'", arg, value), redirect),
                        () -> OutputWriter.writeError(
                                String.format("alias: %s: not found", arg), redirect));
            }
        }
    }

}
