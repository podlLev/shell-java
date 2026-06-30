package util;

import lombok.Getter;

import java.io.File;

@Getter
public class Environment {

    private File currentDir;

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

    public String getHome() {
        return System.getenv("HOME");
    }

}
