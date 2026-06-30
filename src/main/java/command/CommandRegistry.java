package command;

import command.builtin.Builtin;
import command.builtin.EchoBuiltin;
import command.builtin.ExitBuiltin;
import command.builtin.TypeBuiltin;

import java.util.*;

public class CommandRegistry {

    private static final List<Builtin> BUILTINS = List.of(
            ExitBuiltin.INSTANCE,
            EchoBuiltin.INSTANCE
    );

    private final Map<String, Builtin> builtins;

    public CommandRegistry() {
        Map<String, Builtin> map = new HashMap<>();
        for (Builtin b : BUILTINS) {
            map.put(b.name(), b);
        }

        Set<String> builtinNames = Set.copyOf(map.keySet());
        map.put("type", new TypeBuiltin(builtinNames));

        this.builtins = Map.copyOf(map);
    }

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