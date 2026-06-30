package command;

import command.builtin.Builtin;
import command.builtin.EchoBuiltin;
import command.builtin.ExitBuiltin;

import java.util.Map;

public class CommandRegistry {

    private final Map<String, Builtin> builtins = Map.of(
            "exit", ExitBuiltin.INSTANCE
            , "echo", EchoBuiltin.INSTANCE
    );

    public boolean isBuiltin(String name) {
        return builtins.containsKey(name);
    }

    public void execute(String command, String argument) {
        if (isBuiltin(command)) {
            builtins.get(command).execute(argument);
        } else {
            System.out.printf("%s: command not found\n", command);
        }
    }

}