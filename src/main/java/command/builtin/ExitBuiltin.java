package command.builtin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import redirect.Redirect;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExitBuiltin implements Builtin {

    public static final ExitBuiltin INSTANCE = new ExitBuiltin();

    @Override
    public String name() {
        return "exit";
    }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        System.exit(0);
    }

}
