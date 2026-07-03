package command.builtin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EchoBuiltin implements Builtin {

    public static final EchoBuiltin INSTANCE = new EchoBuiltin();

    @Override
    public String name() {
        return "echo";
    }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        OutputWriter.write(String.join(" ", args), redirect);
    }

}
