package completer;

import command.CommandRegistry;
import lombok.RequiredArgsConstructor;
import org.jline.builtins.Completers;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;
import util.Environment;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class CandidateCollector {

    private final Completer builtinCompleter;
    private final Completer binaryCompleter;
    private final Completer filesCompleter;
    private final Completer directoriesCompleter;

    public static CandidateCollector of(CommandRegistry registry, Environment env) {
        return new CandidateCollector(
                new StringsCompleter(registry.getBuiltinNames()),
                new SystemBinaryCompleter(env),
                new Completers.FilesCompleter(env.getCurrentDir()),
                new Completers.DirectoriesCompleter(env.getCurrentDir())
        );
    }

    public List<Candidate> collect(LineReader reader, ParsedLine pl) {
        List<Candidate> candidates = new ArrayList<>();
        if (pl.wordIndex() == 0) {
            builtinCompleter.complete(reader, pl, candidates);
            binaryCompleter.complete(reader, pl, candidates);
        } else {
            filesCompleter.complete(reader, pl, candidates);
            directoriesCompleter.complete(reader, pl, candidates);
        }
        return candidates;
    }

}
