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
        map.put("exit", ExitBuiltin.INSTANCE);
        map.put("echo", EchoBuiltin.INSTANCE);
        map.put("pwd", new PwdBuiltin(env));
        map.put("cd", new CdBuiltin(env));
        map.put("ls", new LsBuiltin(env));
        map.put("mkdir", MkdirBuiltin.INSTANCE);
        map.put("rmdir", RmdirBuiltin.INSTANCE);

        Set<String> builtinNames = new HashSet<>(map.keySet());
        builtinNames.add("type");
        map.put("type", new TypeBuiltin(builtinNames, env));

        this.builtins = Map.copyOf(map);
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