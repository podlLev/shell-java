package command;

import redirect.Redirect;

import java.util.List;

public interface Executable {

    void execute(String command, List<String> args, Redirect redirect);

    default void execute(String command, List<String> args) {
        execute(command, args, null);
    }

}
