package pipe;

import util.ParseResult;

import java.util.List;

public record PipelineResult(
        List<ParseResult> stages,
        boolean background
) {}
