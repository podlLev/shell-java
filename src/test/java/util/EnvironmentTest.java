package util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentTest {

    private Environment env;

    @BeforeEach
    void setUp() {
        env = new Environment();
    }

    @Test
    void initialCurrentDirIsUserDir() {
        assertEquals(new File(System.getProperty("user.dir")), env.getCurrentDir());
    }

    @Test
    void setsCurrentDir() {
        File dir = new File(System.getProperty("java.io.tmpdir"));
        env.setCurrentDir(dir);
        assertEquals(dir, env.getCurrentDir());
    }

    @Test
    void setsAndGetsVariable() {
        env.setVariable("MY_VAR", "hello");
        assertEquals(Optional.of("hello"), env.getVariable("MY_VAR"));
    }

    @Test
    void returnsEmptyForUnknownVariable() {
        assertTrue(env.getVariable("UNKNOWN_VAR_XYZ").isEmpty());
    }

    @Test
    void unsetsVariable() {
        env.setVariable("MY_VAR", "hello");
        env.unsetVariable("MY_VAR");
        assertTrue(env.getVariable("MY_VAR").isEmpty());
    }

    @Test
    void getVariableFallsBackToSystemEnv() {
        assertTrue(env.getVariable("PATH").isPresent());
    }

    @Test
    void localVariableOverridesSystemEnv() {
        env.setVariable("PATH", "custom");
        assertEquals(Optional.of("custom"), env.getVariable("PATH"));
    }

    @Test
    void tracksLastExitCode() {
        env.setLastExitCode(42);
        assertEquals(42, env.getLastExitCode());
    }

    @Test
    void tracksLastBackgroundPid() {
        env.setLastBackgroundPid(12345L);
        assertEquals(12345L, env.getLastBackgroundPid());
    }

    @Test
    void returnsUnmodifiableVariables() {
        assertThrows(UnsupportedOperationException.class,
                () -> env.getVariables().put("test", "value"));
    }

    @Test
    void isWindowsMatchesOs() {
        boolean expected = System.getProperty("os.name").toLowerCase().contains("win");
        assertEquals(expected, env.isWindows());
    }

}
