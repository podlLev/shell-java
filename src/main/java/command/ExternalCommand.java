package command;

import lombok.RequiredArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;
import util.Environment;
import util.PathResolver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ExternalCommand implements Executable {

    private final Environment env;

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        String execPath = PathResolver.find(command, env);
        if (execPath == null) {
            System.err.printf("%s: command not found%n", command);
            return;
        }

        try {
            List<String> parts = buildProcessArgs(command, execPath, args);
            ProcessBuilder pb = new ProcessBuilder(parts);
            pb.directory(env.getCurrentDir());
            configureRedirect(pb, redirect);
            pb.start().waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            OutputWriter.writeError(String.format("%s: interrupted", command), redirect);
        } catch (Exception e) {
            OutputWriter.writeError(String.format("%s: %s", command, e.getMessage()), redirect);
        }
    }

    private void configureRedirect(ProcessBuilder pb, Redirect redirect) {
        if (redirect == null) {
            pb.inheritIO();
            return;
        }
        switch (redirect.type()) {
            case STDOUT -> pb.redirectOutput(new File(redirect.filePath()));
            case STDERR -> pb.redirectError(new File(redirect.filePath()));
        }
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
    }

    private List<String> buildProcessArgs(String command, String execPath, List<String> args) {
        List<String> parts = new ArrayList<>();
        if (env.isWindows()) {
            parts.add("cmd.exe");
            parts.add("/c");
            parts.add(command);
        } else {
            parts.add(execPath);
        }
        parts.addAll(args);
        return parts;
    }

}
