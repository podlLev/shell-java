package jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class JobManager {

    private final List<Job> jobs = new ArrayList<>();
    private int nextJobNumber = 1;

    public Job addJob(Process process, String commandLine) {
        if (jobs.isEmpty()) nextJobNumber = 1;
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

    public List<Job> getJobs() {
        return Collections.unmodifiableList(jobs);
    }

    public List<Job> reapAndGetSnapshot() {
        reap();
        List<Job> snapshot = new ArrayList<>(jobs);
        jobs.removeIf(j -> j.getStatus() == JobStatus.DONE);
        return snapshot;
    }

    public void reap() {
        for (Job job : jobs) {
            if (!job.getProcess().isAlive()) {
                try {
                    job.getProcess().waitFor();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

}
