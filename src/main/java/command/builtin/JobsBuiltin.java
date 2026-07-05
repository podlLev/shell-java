package command.builtin;

import jobs.Job;
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
        env.getJobManager().cleanFinished();
        List<Job> jobs = env.getJobManager().getJobs();
        if (jobs.isEmpty()) return;

        int size = jobs.size();
        for (int i = 0; i < size; i++) {
            Job job = jobs.get(i);
            String marker = (i == size - 1) ? "+" : (i == size - 2) ? "-" : " ";
            String status = switch (job.getStatus()) {
                case RUNNING -> "Running";
                case STOPPED -> "Stopped";
                case DONE -> "Done";
            };
            OutputWriter.write(String.format("[%d]%s  %-24s%s &",
                    job.getJobNumber(), marker, status, job.getCommandLine()), redirect);
        }
    }

}
