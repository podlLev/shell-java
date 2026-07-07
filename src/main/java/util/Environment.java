package util;

import history.HistoryManager;
import jobs.JobManager;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class Environment {

    private File currentDir;

    private final Map<String, String> completions = new HashMap<>();
    private final JobManager jobManager = new JobManager();
    private final HistoryManager historyManager = new HistoryManager(
            System.getenv("HISTFILE"),
            System.getenv("HISTSIZE"),
            System.getenv("HISTCONTROL")
    );
    private final Map<String, String> variables = new HashMap<>();

    @Getter @Setter
    private int lastExitCode = 0;

    @Getter @Setter
    private long lastBackgroundPid = 0;

    public Environment() {
        this.currentDir = new File(System.getProperty("user.dir"));
    }

    public void setCurrentDir(File dir) {
        this.currentDir = dir;
        System.setProperty("user.dir", dir.getAbsolutePath());
    }

    public String getPath() {
        return System.getenv("PATH");
    }

    public String getPathExt() {
        return System.getenv("PATHEXT");
    }

    public String getHome() {
        return System.getenv("HOME");
    }

    public boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public void registerCompletion(String command, String scriptPath) {
        completions.put(command, scriptPath);
    }

    public Optional<String> getCompletion(String command) {
        return Optional.ofNullable(completions.get(command));
    }

    public void removeCompletion(String command) {
        completions.remove(command);
    }

    public String getHistFile() {
        return System.getenv("HISTFILE");
    }

    public String getHistSize() {
        return System.getenv("HISTSIZE");
    }

    public String getHistControl() {
        return System.getenv("HISTCONTROL");
    }

    public void setVariable(String name, String value) {
        variables.put(name, value);
    }

    public Optional<String> getVariable(String name) {
        if (variables.containsKey(name)) {
            return Optional.of(variables.get(name));
        }
        return Optional.ofNullable(System.getenv(name));
    }

    public void unsetVariable(String name) {
        variables.remove(name);
    }

    public Map<String, String> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

}
