package command;

import jobs.Job;
import lombok.RequiredArgsConstructor;
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
        execute(command, args, redirect, false);
    }

    public void execute(String command, List<String> args, Redirect redirect, boolean background) {
        String execPath = PathResolver.find(command, env);
        if (execPath == null) {
            System.err.printf("%s: command not found%n", command);
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(buildProcessArgs(command, execPath, args));
            pb.directory(env.getCurrentDir());
            configureRedirect(pb, redirect);

            Process process = pb.start();

            if (background) {
                String commandLine = command + (args.isEmpty() ? "" : " " + String.join(" ", args));
                Job job = env.getJobManager().addJob(process, commandLine);
                System.out.printf("[%d] %d%n", job.getJobNumber(), job.getPid());
            } else {
                process.waitFor();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.printf("%s: interrupted%n", command);
        } catch (Exception e) {
            System.err.printf("%s: %s%n", command, e.getMessage());
        }
    }

    private void configureRedirect(ProcessBuilder pb, Redirect redirect) {
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
