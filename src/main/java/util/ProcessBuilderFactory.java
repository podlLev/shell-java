package util;

import redirect.Redirect;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class ProcessBuilderFactory {

    private ProcessBuilderFactory() {}

    public static ProcessBuilder build(String command, String execPath, List<String> args, Environment env) {
        List<String> parts = new ArrayList<>();
        if (env.isWindows()) {
            parts.add("cmd.exe");
            parts.add("/c");
            parts.add(command);
        } else {
            parts.add(execPath);
        }
        parts.addAll(args);
        return new ProcessBuilder(parts);
    }

    public static ProcessBuilder build(String command, List<String> args, Environment env) {
        String execPath = PathResolver.find(command, env);
        return build(command, execPath != null ? execPath : command, args, env);
    }

    public static void configureOutput(ProcessBuilder pb, Redirect redirect) {
        if (redirect == null) {
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            return;
        }
        switch (redirect.type()) {
            case STDOUT -> pb.redirectOutput(new File(redirect.filePath()));
            case STDOUT_APPEND -> pb.redirectOutput(
                    ProcessBuilder.Redirect.appendTo(new File(redirect.filePath())));
            case STDERR -> pb.redirectError(new File(redirect.filePath()));
            case STDERR_APPEND -> pb.redirectError(
                    ProcessBuilder.Redirect.appendTo(new File(redirect.filePath())));
            default -> pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        }
    }

    public static void configureRedirect(ProcessBuilder pb, Redirect redirect) {
        if (redirect == null) {
            pb.inheritIO();
            return;
        }
        switch (redirect.type()) {
            case STDOUT -> {
                pb.redirectOutput(new File(redirect.filePath()));
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            }
            case STDOUT_APPEND -> {
                pb.redirectOutput(ProcessBuilder.Redirect.appendTo(new File(redirect.filePath())));
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            }
            case STDERR -> {
                pb.redirectError(new File(redirect.filePath()));
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            }
            case STDERR_APPEND -> {
                pb.redirectError(ProcessBuilder.Redirect.appendTo(new File(redirect.filePath())));
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            }
            default -> pb.inheritIO();
        }
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
    }

}
