package util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class PathResolverTest {

    @TempDir
    Path tempDir;

    @Test
    void findsExecutableInPath() throws IOException {
        Path exe = tempDir.resolve("mycommand.exe");
        Files.writeString(exe, "");
        exe.toFile().setExecutable(true);

        Environment env = new Environment() {
            @Override public String getPath() { return tempDir.toString(); }
            @Override public String getPathExt() { return ".exe"; }
            @Override public boolean isWindows() { return true; }
        };

        String result = PathResolver.find("mycommand", env);
        assertNotNull(result);
        assertTrue(result.contains("mycommand"));
    }

    @Test
    void returnsNullWhenNotFound() {
        Environment env = new Environment() {
            @Override public String getPath() { return tempDir.toString(); }
            @Override public String getPathExt() { return ".exe"; }
            @Override public boolean isWindows() { return true; }
        };

        assertNull(PathResolver.find("nonexistent", env));
    }

    @Test
    void returnsNullWhenPathIsNull() {
        Environment env = new Environment() {
            @Override public String getPath() { return null; }
        };
        assertNull(PathResolver.find("anything", env));
    }

    @Test
    void returnsNullForNonExecutable() throws IOException {
        assumeFalse(System.getProperty("os.name").toLowerCase().contains("win"),
                "Skipping on Windows: no Unix-style execute bits");

        Path file = tempDir.resolve("notexec");
        Files.writeString(file, "");
        file.toFile().setExecutable(false);

        Environment env = new Environment() {
            @Override public String getPath() { return tempDir.toString(); }
            @Override public String getPathExt() { return null; }
            @Override public boolean isWindows() { return false; }
        };

        assertNull(PathResolver.find("notexec", env));
    }

}
