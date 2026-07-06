package util;

import history.HistoryManager;
import jobs.JobManager;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class Environment {

    private File currentDir;

    private final Map<String, String> completions = new HashMap<>();
    private final JobManager jobManager = new JobManager();
    private final HistoryManager historyManager = new HistoryManager();

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

}
