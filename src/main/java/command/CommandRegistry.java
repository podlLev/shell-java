package command;

import command.builtin.*;
import util.Environment;

import java.util.*;

public class CommandRegistry {

    private final Map<String, Builtin> builtins;
    private final Environment env;

    public CommandRegistry(Environment env) {
        this.env = env;

        Map<String, Builtin> map = new HashMap<>();
        register(map, ExitBuiltin.INSTANCE);
        register(map, EchoBuiltin.INSTANCE);
        register(map, new PwdBuiltin(env));
        register(map, new CdBuiltin(env));
        register(map, new LsBuiltin(env));
        register(map, MkdirBuiltin.INSTANCE);
        register(map, RmdirBuiltin.INSTANCE);
        register(map, TouchBuiltin.INSTANCE);

        Set<String> builtinNames = new HashSet<>(map.keySet());
        builtinNames.add("type");
        register(map, new TypeBuiltin(builtinNames, env));

        this.builtins = Map.copyOf(map);
    }

    private static void register(Map<String, Builtin> map, Builtin builtin) {
        map.put(builtin.name(), builtin);
    }

    public boolean isBuiltin(String name) {
        return builtins.containsKey(name);
    }

    public void execute(String command, List<String> arguments) {
        if (isBuiltin(command)) {
            builtins.get(command).execute(command, arguments);
        } else {
            new ExternalCommand(env).execute(command, arguments);
        }
    }

}