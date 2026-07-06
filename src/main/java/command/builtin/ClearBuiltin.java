package command.builtin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import redirect.Redirect;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClearBuiltin implements Builtin {

    public static final ClearBuiltin INSTANCE = new ClearBuiltin();

    @Override
    public String name() {
        return "clear";
    }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}
