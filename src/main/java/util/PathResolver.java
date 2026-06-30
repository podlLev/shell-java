package util;

import java.io.File;

public class PathResolver {

    public static String find(String command, String pathEnv) {
        if (pathEnv == null) return null;

        for (String dir : pathEnv.split(":")) {
            File file = new File(dir, command);
            if (file.exists() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

}
