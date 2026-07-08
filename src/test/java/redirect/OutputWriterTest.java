package redirect;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OutputWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void writesToStdoutWhenNoRedirect() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        OutputWriter.write("hello", null);
        System.setOut(System.out);
        assertTrue(baos.toString().contains("hello"));
    }

    @Test
    void writesToFileOnStdoutRedirect() throws IOException {
        Path file = tempDir.resolve("out.txt");
        Redirect redirect = new Redirect(RedirectType.STDOUT, file.toString());
        OutputWriter.write("hello", redirect);
        List<String> lines = Files.readAllLines(file);
        assertEquals(1, lines.size());
        assertEquals("hello", lines.get(0));
    }

    @Test
    void appendsToFileOnAppendRedirect() throws IOException {
        Path file = tempDir.resolve("out.txt");
        Files.writeString(file, "line1\n");
        Redirect redirect = new Redirect(RedirectType.STDOUT_APPEND, file.toString());
        OutputWriter.write("line2", redirect);
        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());
        assertEquals("line2", lines.get(1));
    }

    @Test
    void writesErrorToStderrWhenNoRedirect() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setErr(new PrintStream(baos));
        OutputWriter.writeError("error message", null);
        System.setErr(System.err);
        assertTrue(baos.toString().contains("error message"));
    }

    @Test
    void writesErrorToFileOnStderrRedirect() throws IOException {
        Path file = tempDir.resolve("err.txt");
        Redirect redirect = new Redirect(RedirectType.STDERR, file.toString());
        OutputWriter.writeError("error message", redirect);
        List<String> lines = Files.readAllLines(file);
        assertEquals(1, lines.size());
        assertEquals("error message", lines.get(0));
    }

}