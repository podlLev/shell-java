package command.builtin;

import java.util.Set;

public final class TypeBuiltin implements Builtin {

    private final Set<String> builtinNames;

    public TypeBuiltin(Set<String> builtinNames) {
        this.builtinNames = builtinNames;
    }

    @Override
    public String name() {
        return "type";
    }

    @Override
    public void execute(String arg) {
        if (builtinNames.contains(arg)) {
            System.out.printf("%s is a builtin\n", arg);
        } else {
            System.out.printf("%s: not found\n", arg);
        }
    }

}
