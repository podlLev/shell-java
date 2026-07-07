package command;

import jobs.Job;
import lombok.RequiredArgsConstructor;
import redirect.Redirect;
import util.Environment;
import util.PathResolver;
import util.ProcessBuilderFactory;

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
            ProcessBuilder pb = ProcessBuilderFactory.build(command, execPath, args, env);
            pb.directory(env.getCurrentDir());
            ProcessBuilderFactory.configureRedirect(pb, redirect);

            Process process = pb.start();

            if (background) {
                String commandLine = command + (args.isEmpty() ? "" : " " + String.join(" ", args));
                Job job = env.getJobManager().addJob(process, commandLine);
                env.setLastBackgroundPid(job.getPid());
                System.out.printf("[%d] %d%n", job.getJobNumber(), job.getPid());
            } else {
                int exitCode = process.waitFor();
                env.setLastExitCode(exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.printf("%s: interrupted%n", command);
        } catch (Exception e) {
            System.err.printf("%s: %s%n", command, e.getMessage());
        }
    }

}
