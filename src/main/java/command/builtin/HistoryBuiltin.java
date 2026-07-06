package command.builtin;

import redirect.Redirect;

import java.util.List;

public final class HistoryBuiltin implements Builtin {

    public static final HistoryBuiltin INSTANCE = new HistoryBuiltin();

    @Override
    public String name() {
        return "history";
    }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {

    }

}
