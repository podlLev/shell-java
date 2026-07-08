package jobs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobManagerTest {

    private JobManager manager;

    @BeforeEach
    void setUp() {
        manager = new JobManager();
    }

    @Test
    void addsJob() {
        Process process = mock(Process.class);
        when(process.isAlive()).thenReturn(true);

        Job job = manager.addJob(process, "ping");
        assertEquals(1, job.getJobNumber());
        assertEquals(1, manager.getJobs().size());
    }

    @Test
    void removesJob() {
        Process process = mock(Process.class);
        when(process.isAlive()).thenReturn(true);

        Job job = manager.addJob(process, "ping");
        manager.removeJob(job);
        assertTrue(manager.getJobs().isEmpty());
    }

    @Test
    void findsJobByNumber() {
        Process process = mock(Process.class);
        when(process.isAlive()).thenReturn(true);

        Job job = manager.addJob(process, "ping");
        Optional<Job> found = manager.getJob(1);
        assertTrue(found.isPresent());
        assertEquals(job, found.get());
    }

    @Test
    void returnsEmptyForUnknownJobNumber() {
        Optional<Job> found = manager.getJob(99);
        assertTrue(found.isEmpty());
    }

    @Test
    void resetsJobNumberWhenEmpty() {
        Process process = mock(Process.class);
        when(process.isAlive()).thenReturn(true);

        Job job1 = manager.addJob(process, "ping");
        manager.removeJob(job1);
        Job job2 = manager.addJob(process, "ping");
        assertEquals(1, job2.getJobNumber());
    }

    @Test
    void reapsFinishedJobs() throws InterruptedException {
        Process process = mock(Process.class);
        when(process.isAlive()).thenReturn(false);
        when(process.waitFor()).thenReturn(0);

        manager.addJob(process, "ping");
        manager.reap();
        verify(process).waitFor();
    }

}
