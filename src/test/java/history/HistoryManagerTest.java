package history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private HistoryManager manager;

    @BeforeEach
    void setUp() {
        manager = new HistoryManager(null, null, null);
    }

    @Test
    void addsEntries() {
        manager.add("echo hello");
        manager.add("echo world");
        assertEquals(2, manager.getEntries().size());
    }

    @Test
    void suppressesConsecutiveDuplicatesWithIgnoreDups() {
        manager = new HistoryManager(null, null, "ignoredups");
        manager.add("echo hello");
        manager.add("echo hello");
        assertEquals(1, manager.getEntries().size());
    }

    @Test
    void allowsConsecutiveDuplicatesWithoutHistControl() {
        manager.add("echo hello");
        manager.add("echo hello");
        assertEquals(2, manager.getEntries().size());
    }

    @Test
    void allowsNonConsecutiveDuplicates() {
        manager.add("echo hello");
        manager.add("echo world");
        manager.add("echo hello");
        assertEquals(3, manager.getEntries().size());
    }

    @Test
    void respectsHistSize() {
        manager = new HistoryManager(null, "3", null);
        manager.add("one");
        manager.add("two");
        manager.add("three");
        manager.add("four");
        assertEquals(3, manager.getEntries().size());
        assertEquals("two", manager.getEntries().get(0));
    }

    @Test
    void respectsIgnoreDups() {
        manager = new HistoryManager(null, null, "ignoredups");
        manager.add("echo hello");
        manager.add("echo hello");
        assertEquals(1, manager.getEntries().size());
    }

    @Test
    void respectsIgnoreSpace() {
        manager = new HistoryManager(null, null, "ignorespace");
        manager.add(" echo hidden");
        manager.add("echo visible");
        assertEquals(1, manager.getEntries().size());
        assertEquals("echo visible", manager.getEntries().get(0));
    }

    @Test
    void respectsEraseDups() {
        manager = new HistoryManager(null, null, "erasedups");
        manager.add("echo first");
        manager.add("echo second");
        manager.add("echo first");
        assertEquals(2, manager.getEntries().size());
        assertEquals("echo second", manager.getEntries().get(0));
        assertEquals("echo first", manager.getEntries().get(1));
    }

    @Test
    void clearsEntries() {
        manager.add("echo hello");
        manager.add("echo world");
        manager.clear();
        assertTrue(manager.getEntries().isEmpty());
    }

    @Test
    void savesAndLoads(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("history");
        manager.add("echo hello");
        manager.add("echo world");
        manager.writeTo(file);

        HistoryManager loaded = new HistoryManager(file.toString(), null, null);
        loaded.load();
        assertEquals(2, loaded.getEntries().size());
        assertEquals("echo hello", loaded.getEntries().get(0));
    }

    @Test
    void loadsIgnoresBlankLines(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("history");
        Files.writeString(file, "echo hello\n\n   \necho world\n");

        manager = new HistoryManager(file.toString(), null, null);
        manager.load();
        assertEquals(2, manager.getEntries().size());
    }

}
