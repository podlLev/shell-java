package command.builtin;

import lombok.RequiredArgsConstructor;
import util.Environment;

import java.util.List;

@RequiredArgsConstructor
public class PwdBuiltin implements Builtin {

    private final Environment env;

    @Override
    public String name() {
        return "pwd";
    }

    @Override
    public void execute(String command, List<String> args) {
        System.out.println(env.getCurrentDir());
    }

}
