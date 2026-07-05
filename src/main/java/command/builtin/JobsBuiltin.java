package command.builtin;

import jobs.Job;
import jobs.JobStatus;
import lombok.RequiredArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;
import util.Environment;

import java.util.List;

@RequiredArgsConstructor
public final class JobsBuiltin implements Builtin {

    private final Environment env;

    @Override
    public String name() {
        return "jobs";
    }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        List<Job> jobs = env.getJobManager().reapAndGetSnapshot();
        if (jobs.isEmpty()) return;

        int size = jobs.size();
        int currentJobNumber = jobs.get(size - 1).getJobNumber();
        int previousJobNumber = size >= 2 ? jobs.get(size - 2).getJobNumber() : -1;

        for (Job job : jobs) {
            String marker = job.getJobNumber() == currentJobNumber ? "+"
                    : job.getJobNumber() == previousJobNumber ? "-"
                    : " ";

            JobStatus status = job.getStatus();
            String statusText = switch (status) {
                case RUNNING -> "Running";
                case STOPPED -> "Stopped";
                case DONE -> "Done";
            };
            String suffix = status == JobStatus.RUNNING ? " &" : "";

            OutputWriter.write(String.format("[%d]%s  %-24s%s%s",
                    job.getJobNumber(), marker, statusText, job.getCommandLine(), suffix), redirect);
        }
    }

}
