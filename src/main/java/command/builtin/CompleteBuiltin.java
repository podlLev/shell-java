package command.builtin;

import lombok.RequiredArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;
import util.Environment;

import java.util.List;

@RequiredArgsConstructor
public final class CompleteBuiltin implements Builtin {

    private final Environment env;

    @Override
    public String name() {
        return "complete";
    }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        if (args.isEmpty()) return;

        if (args.get(0).equals("-p") && args.size() >= 2) {
            handlePrint(args.get(1), redirect);
            return;
        }

        if (args.get(0).equals("-C") && args.size() >= 3) {
            handleRegister(args.get(1), args.get(2));
        }
    }

    private void handlePrint(String target, Redirect redirect) {
        env.getCompletion(target).ifPresentOrElse(
                script -> OutputWriter.write("complete -C '" + script + "' " + target, redirect),
                () -> OutputWriter.writeError("complete: " + target + ": no completion specification", redirect)
        );
    }

    private void handleRegister(String scriptPath, String commandName) {
        env.registerCompletion(commandName, scriptPath);
    }

}
