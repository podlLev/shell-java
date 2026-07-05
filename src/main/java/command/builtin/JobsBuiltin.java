package command.builtin;

import jobs.Job;
import jobs.JobStatus;
import lombok.RequiredArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;
import util.Environment;

import java.util.ArrayList;
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
        List<Job> jobs = env.getJobManager().getJobs();
        if (jobs.isEmpty()) return;

        int size = jobs.size();
        int currentJobNumber = jobs.get(size - 1).getJobNumber();
        int previousJobNumber = size >= 2 ? jobs.get(size - 2).getJobNumber() : -1;

        List<Job> toRemove = new ArrayList<>();

        for (Job job : jobs) {
            String marker;
            if (job.getJobNumber() == currentJobNumber) {
                marker = "+";
            } else if (job.getJobNumber() == previousJobNumber) {
                marker = "-";
            } else {
                marker = " ";
            }

            JobStatus status = job.getStatus();
            String statusText = switch (status) {
                case RUNNING -> "Running";
                case STOPPED -> "Stopped";
                case DONE -> "Done";
            };
            String suffix = status == JobStatus.RUNNING ? " &" : "";

            OutputWriter.write(String.format("[%d]%s  %-24s%s%s",
                    job.getJobNumber(), marker, statusText, job.getCommandLine(), suffix), redirect);

            if (status == JobStatus.DONE) {
                toRemove.add(job);
            }
        }

        toRemove.forEach(env.getJobManager()::removeJob);
    }

}
