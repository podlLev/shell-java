package alias;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AliasManagerTest {

    private AliasManager manager;

    @BeforeEach
    void setUp() {
        manager = new AliasManager();
    }

    @Test
    void setsAndGetsAlias() {
        manager.set("ll", "ls");
        assertEquals(Optional.of("ls"), manager.get("ll"));
    }

    @Test
    void returnsEmptyForUnknownAlias() {
        assertTrue(manager.get("unknown").isEmpty());
    }

    @Test
    void removesAlias() {
        manager.set("ll", "ls");
        manager.remove("ll");
        assertTrue(manager.get("ll").isEmpty());
    }

    @Test
    void clearsAllAliases() {
        manager.set("ll", "ls");
        manager.set("say", "echo");
        manager.clear();
        assertTrue(manager.getAll().isEmpty());
    }

    @Test
    void expandsSimpleAlias() {
        manager.set("ll", "ls");
        assertEquals("ls", manager.expand("ll"));
    }

    @Test
    void expandsAliasWithArgs() {
        manager.set("say", "echo");
        assertEquals("echo hello world", manager.expand("say hello world"));
    }

    @Test
    void expandsRecursiveAlias() {
        manager.set("a", "echo hello");
        manager.set("b", "a");
        assertEquals("echo hello", manager.expand("b"));
    }

    @Test
    void handlesSelfReferencingAlias() {
        manager.set("ls", "ls --color");
        assertEquals("ls --color", manager.expand("ls"));
    }

    @Test
    void returnsInputWhenNoAlias() {
        assertEquals("echo hello", manager.expand("echo hello"));
    }

    @Test
    void preventsInfiniteRecursion() {
        manager.set("a", "b");
        manager.set("b", "a");
        assertDoesNotThrow(() -> manager.expand("a"));
    }

}
