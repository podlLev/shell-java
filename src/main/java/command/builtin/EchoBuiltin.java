package command.builtin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EchoBuiltin implements Builtin {

    public static final EchoBuiltin INSTANCE = new EchoBuiltin();

    @Override
    public String name() {
        return "echo";
    }

    @Override
    public void execute(String command, List<String> args) {
        System.out.println(String.join(" ", args));
    }

}
