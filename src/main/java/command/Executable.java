package command;

import java.util.List;

public interface Executable {

    void execute(String command, List<String> args);

}
