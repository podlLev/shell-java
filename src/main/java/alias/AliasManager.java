package alias;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class AliasManager {

    private final Map<String, String> aliases = new LinkedHashMap<>();

    public void set(String name, String value) {
        aliases.put(name, value);
    }

    public Optional<String> get(String name) {
        return Optional.ofNullable(aliases.get(name));
    }

    public void remove(String name) {
        aliases.remove(name);
    }

    public void clear() {
        aliases.clear();
    }

    public Map<String, String> getAll() {
        return Collections.unmodifiableMap(aliases);
    }

    public String expand(String input) {
        return expand(input, 0);
    }

    private String expand(String input, int depth) {
        if (depth > 10) return input;

        String command = input.split("\\s+")[0];
        Optional<String> alias = get(command);

        if (alias.isEmpty()) return input;

        String expanded = alias.get() + input.substring(command.length());

        String newCommand = expanded.split("\\s+")[0];
        if (newCommand.equals(command)) return expanded;

        return expand(expanded, depth + 1);
    }

}
