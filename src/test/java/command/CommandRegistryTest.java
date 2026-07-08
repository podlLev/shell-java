package command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Environment;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CommandRegistryTest {

    private CommandRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new CommandRegistry(new Environment());
    }

    @Test
    void recognizesBuiltins() {
        assertTrue(registry.isBuiltin("echo"));
        assertTrue(registry.isBuiltin("exit"));
        assertTrue(registry.isBuiltin("pwd"));
        assertTrue(registry.isBuiltin("cd"));
        assertTrue(registry.isBuiltin("type"));
        assertTrue(registry.isBuiltin("ls"));
        assertTrue(registry.isBuiltin("mkdir"));
        assertTrue(registry.isBuiltin("rmdir"));
        assertTrue(registry.isBuiltin("touch"));
        assertTrue(registry.isBuiltin("cat"));
        assertTrue(registry.isBuiltin("history"));
        assertTrue(registry.isBuiltin("alias"));
        assertTrue(registry.isBuiltin("unalias"));
    }

    @Test
    void doesNotRecognizeUnknownAsBuiltin() {
        assertFalse(registry.isBuiltin("git"));
        assertFalse(registry.isBuiltin("nonexistent"));
    }

    @Test
    void returnsBuiltinNames() {
        Set<String> names = registry.getBuiltinNames();
        assertTrue(names.contains("echo"));
        assertTrue(names.contains("exit"));
        assertTrue(names.contains("alias"));
    }

    @Test
    void buildinNamesAreUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
                () -> registry.getBuiltinNames().add("fake"));
    }

}
