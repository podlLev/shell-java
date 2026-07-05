package command;

import command.builtin.*;
import redirect.Redirect;
import util.Environment;

import java.util.*;

public class CommandRegistry {

    private final Map<String, Builtin> builtins;
    private final ExternalCommand externalCommand;

    public CommandRegistry(Environment env) {
        Map<String, Builtin> map = new HashMap<>();
        for (BuiltinFactory f : BuiltinFactory.values()) {
            register(map, f.create(env));
        }

        Set<String> builtinNames = new HashSet<>(map.keySet());
        builtinNames.add("type");
        register(map, new TypeBuiltin(builtinNames, env));

        this.builtins = Map.copyOf(map);
        this.externalCommand = new ExternalCommand(env);
    }

    private static void register(Map<String, Builtin> map, Builtin builtin) {
        map.put(builtin.name(), builtin);
    }

    public boolean isBuiltin(String name) {
        return builtins.containsKey(name);
    }

    public void execute(String command, List<String> arguments, Redirect redirect, boolean background) {
        if (isBuiltin(command)) {
            builtins.get(command).execute(command, arguments, redirect);
        } else {
            externalCommand.execute(command, arguments, redirect, background);
        }
    }

    public Set<String> getBuiltinNames() {
        return builtins.keySet();
    }

}