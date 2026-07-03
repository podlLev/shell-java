package command.builtin;

import lombok.RequiredArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;
import util.Environment;

import java.util.List;

@RequiredArgsConstructor
public final class PwdBuiltin implements Builtin {

    private final Environment env;

    @Override
    public String name() {
        return "pwd";
    }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        OutputWriter.write(env.getCurrentDir().toString(), redirect);
    }

}
