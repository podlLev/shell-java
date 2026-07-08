package pipe;

import org.junit.jupiter.api.Test;
import util.ParseResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PipelineResultTest {

    @Test
    void createsSingleStage() {
        ParseResult stage = new ParseResult(List.of("echo", "hello"), null, false);
        PipelineResult result = new PipelineResult(List.of(stage), false);
        assertEquals(1, result.stages().size());
        assertFalse(result.background());
    }

    @Test
    void createsMultipleStages() {
        ParseResult stage1 = new ParseResult(List.of("echo", "hello"), null, false);
        ParseResult stage2 = new ParseResult(List.of("cat"), null, false);
        PipelineResult result = new PipelineResult(List.of(stage1, stage2), false);
        assertEquals(2, result.stages().size());
    }

    @Test
    void createsWithBackground() {
        ParseResult stage = new ParseResult(List.of("ping"), null, false);
        PipelineResult result = new PipelineResult(List.of(stage), true);
        assertTrue(result.background());
    }

}
