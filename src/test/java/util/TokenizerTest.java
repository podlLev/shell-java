package util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pipe.PipelineResult;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    private Tokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tokenizer = new Tokenizer(new Environment());
    }

    @Test
    void tokenizesSimpleCommand() {
        PipelineResult result = tokenizer.tokenize("echo hello");
        assertEquals(1, result.stages().size());
        assertEquals("echo", result.stages().get(0).tokens().get(0));
        assertEquals("hello", result.stages().get(0).tokens().get(1));
    }

    @Test
    void tokenizesSingleQuotes() {
        PipelineResult result = tokenizer.tokenize("echo 'hello world'");
        assertEquals("hello world", result.stages().get(0).tokens().get(1));
    }

    @Test
    void tokenizesDoubleQuotes() {
        PipelineResult result = tokenizer.tokenize("echo \"hello world\"");
        assertEquals("hello world", result.stages().get(0).tokens().get(1));
    }

    @Test
    void tokenizesBackslashEscape() {
        PipelineResult result = tokenizer.tokenize("echo hello\\ world");
        assertEquals("hello world", result.stages().get(0).tokens().get(1));
    }

    @Test
    void tokenizesEmptyQuotes() {
        PipelineResult result = tokenizer.tokenize("echo \"\"");
        assertEquals("", result.stages().get(0).tokens().get(1));
    }

    @Test
    void tokenizesPipe() {
        PipelineResult result = tokenizer.tokenize("echo hello | cat");
        assertEquals(2, result.stages().size());
        assertEquals("echo", result.stages().get(0).tokens().get(0));
        assertEquals("cat", result.stages().get(1).tokens().get(0));
    }

    @Test
    void tokenizesMultiplePipes() {
        PipelineResult result = tokenizer.tokenize("echo hello | cat | cat");
        assertEquals(3, result.stages().size());
    }

    @Test
    void tokenizesPipeInsideQuotesNotSplit() {
        PipelineResult result = tokenizer.tokenize("echo 'hello | world'");
        assertEquals(1, result.stages().size());
        assertEquals("hello | world", result.stages().get(0).tokens().get(1));
    }

    @Test
    void detectsBackgroundOperator() {
        PipelineResult result = tokenizer.tokenize("echo hello &");
        assertTrue(result.background());
    }

    @Test
    void detectsStdoutRedirect() {
        PipelineResult result = tokenizer.tokenize("echo hello > out.txt");
        assertNotNull(result.stages().get(0).redirect());
        assertEquals("out.txt", result.stages().get(0).redirect().filePath());
    }

    @Test
    void detectsStderrRedirect() {
        PipelineResult result = tokenizer.tokenize("cat file.txt 2> err.txt");
        assertNotNull(result.stages().get(0).redirect());
    }

    @Test
    void throwsOnUnterminatedSingleQuote() {
        assertThrows(UnterminatedQuoteException.class,
                () -> tokenizer.tokenize("echo 'hello"));
    }

    @Test
    void throwsOnUnterminatedDoubleQuote() {
        assertThrows(UnterminatedQuoteException.class,
                () -> tokenizer.tokenize("echo \"hello"));
    }

}
