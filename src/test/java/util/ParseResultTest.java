package util;

import org.junit.jupiter.api.Test;
import redirect.Redirect;
import redirect.RedirectType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParseResultTest {

    @Test
    void createsWithTokensAndNullRedirect() {
        ParseResult result = new ParseResult(List.of("echo", "hello"), null, false);
        assertEquals(List.of("echo", "hello"), result.tokens());
        assertNull(result.redirect());
        assertFalse(result.background());
    }

    @Test
    void createsWithRedirect() {
        Redirect redirect = new Redirect(RedirectType.STDOUT, "out.txt");
        ParseResult result = new ParseResult(List.of("echo"), redirect, false);
        assertNotNull(result.redirect());
        assertEquals("out.txt", result.redirect().filePath());
    }

    @Test
    void createsWithBackground() {
        ParseResult result = new ParseResult(List.of("ping"), null, true);
        assertTrue(result.background());
    }

}
