package command.builtin;

import redirect.Redirect;

import java.util.List;

public final class JobsBuiltin implements Builtin {

    public static final JobsBuiltin INSTANCE = new JobsBuiltin();

    @Override
    public String name() {
        return "jobs";
    }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {

    }

}
