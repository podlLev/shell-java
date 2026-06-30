package util;

import java.io.File;

public class PathResolver {

    public static String find(String command, Environment env) {
        String pathEnv = env.getPath();
        if (pathEnv == null || command == null || command.isBlank()) {
            return null;
        }

        String[] extensions = resolveExtensions(command, env.getPathExt());

        for (String dir : pathEnv.split(File.pathSeparator)) {
            for (String ext : extensions) {
                File file = new File(dir, command + ext);
                if (file.isFile() && file.canExecute()) {
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
    }

    private static String[] resolveExtensions(String command, String pathExtEnv) {
        if (command.contains(".")) {
            return new String[]{""};
        }
        if (pathExtEnv == null || pathExtEnv.isBlank()) {
            return new String[]{""};
        }
        return pathExtEnv.split(File.pathSeparator);
    }

}
