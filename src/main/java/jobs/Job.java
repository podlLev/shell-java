package jobs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Job {

    private final int jobNumber;
    private final Process process;
    private final String commandLine;

    public long getPid() {
        return process.pid();
    }

    public boolean isRunning() {
        return getStatus() == JobStatus.RUNNING;
    }

    public JobStatus getStatus() {
        if (process.isAlive()) return JobStatus.RUNNING;
        return JobStatus.DONE;
    }

}
