package command;

import lombok.RequiredArgsConstructor;
import redirect.Redirect;
import redirect.RedirectType;
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
            System.out.printf("%s: command not found%n", command);
            return;
        }

        try {
            List<String> parts = buildProcessArgs(command, execPath, args);

            ProcessBuilder pb = new ProcessBuilder(parts);
            if (redirect != null && redirect.type() == RedirectType.STDOUT) {
                pb.redirectOutput(new File(redirect.filePath()));
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            } else {
                pb.inheritIO();
            }
            pb.start().waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("%s: interrupted%n", command);
        } catch (Exception e) {
            System.out.printf("%s: %s%n", command, e.getMessage());
        }
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
