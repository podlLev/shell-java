package jobs;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JobManager {

    @Getter
    private final List<Job> jobs = new ArrayList<>();
    private int nextJobNumber = 1;

    public Job addJob(Process process, String commandLine) {
        Job job = new Job(nextJobNumber++, process, commandLine);
        jobs.add(job);
        return job;
    }

    public void removeJob(Job job) {
        jobs.remove(job);
    }

    public Optional<Job> getJob(int jobNumber) {
        return jobs.stream()
                .filter(j -> j.getJobNumber() == jobNumber)
                .findFirst();
    }

    public void cleanFinished() {
        jobs.removeIf(j -> !j.isRunning());
    }

}
