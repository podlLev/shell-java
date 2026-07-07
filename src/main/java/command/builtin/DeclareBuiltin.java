package command.builtin;

import lombok.RequiredArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;
import util.Environment;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public final class DeclareBuiltin implements Builtin {

    private final Environment env;

    @Override
    public String name() {
        return "declare";
    }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        if (args.isEmpty()) {
            env.getVariables().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> OutputWriter.write(
                            String.format("declare -- %s=\"%s\"", e.getKey(), e.getValue()),
                            redirect));
            return;
        }

        for (String arg : args) {
            if (arg.contains("=")) {
                int eq = arg.indexOf('=');
                String name = arg.substring(0, eq);
                String value = arg.substring(eq + 1);
                env.setVariable(name, value);
            } else {
                env.getVariable(arg).ifPresentOrElse(
                        value -> OutputWriter.write(
                                String.format("declare -- %s=\"%s\"", arg, value), redirect),
                        () -> OutputWriter.writeError(
                                String.format("declare: %s: not found", arg), redirect));
            }
        }
    }

}
