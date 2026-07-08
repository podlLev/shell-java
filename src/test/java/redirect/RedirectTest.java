package redirect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RedirectTest {

    @Test
    void createsRedirectWithTypeAndPath() {
        Redirect redirect = new Redirect(RedirectType.STDOUT, "out.txt");
        assertEquals(RedirectType.STDOUT, redirect.type());
        assertEquals("out.txt", redirect.filePath());
    }

    @Test
    void throwsOnNullFilePath() {
        assertThrows(IllegalArgumentException.class,
                () -> new Redirect(RedirectType.STDOUT, null));
    }

    @Test
    void throwsOnBlankFilePath() {
        assertThrows(IllegalArgumentException.class,
                () -> new Redirect(RedirectType.STDOUT, "  "));
    }

    @Test
    void equalityBasedOnFields() {
        Redirect r1 = new Redirect(RedirectType.STDOUT, "out.txt");
        Redirect r2 = new Redirect(RedirectType.STDOUT, "out.txt");
        assertEquals(r1, r2);
    }

    @Test
    void differentTypeNotEqual() {
        Redirect r1 = new Redirect(RedirectType.STDOUT, "out.txt");
        Redirect r2 = new Redirect(RedirectType.STDERR, "out.txt");
        assertNotEquals(r1, r2);
    }

}
