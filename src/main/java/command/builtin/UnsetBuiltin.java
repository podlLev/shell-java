package command.builtin;

import lombok.RequiredArgsConstructor;
import redirect.Redirect;
import util.Environment;

import java.util.List;

@RequiredArgsConstructor
public final class UnsetBuiltin implements Builtin {

    private final Environment env;

    @Override
    public String name() {
        return "unset";
    }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        if (args.isEmpty()) {
            System.err.println("unset: missing operand");
            return;
        }
        args.forEach(env::unsetVariable);
    }

}
