package command.builtin;

import java.util.List;

public final class EchoBuiltin implements Builtin {

    public static final EchoBuiltin INSTANCE = new EchoBuiltin();

    private EchoBuiltin() {}

    @Override
    public String name() {
        return "echo";
    }

    @Override
    public void execute(String command, List<String> args) {
        System.out.println(String.join(" ", args));
    }

}
