package command.builtin;

import lombok.RequiredArgsConstructor;
import redirect.Redirect;
import util.Environment;

import java.util.List;

@RequiredArgsConstructor
public final class UnaliasBuiltin implements Builtin {

    private final Environment env;

    @Override
    public String name() {
        return "unalias";
    }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        if (args.isEmpty()) {
            System.err.println("unalias: missing operand");
            return;
        }

        if (args.get(0).equals("-a")) {
            env.getAliasManager().clear();
            return;
        }

        for (String arg : args) {
            if (env.getAliasManager().get(arg).isEmpty()) {
                System.err.printf("unalias: %s: not found%n", arg);
            } else {
                env.getAliasManager().remove(arg);
            }
        }
    }

}
