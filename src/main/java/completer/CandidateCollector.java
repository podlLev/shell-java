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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public final class CandidateCollector {

    private final Completer builtinCompleter;
    private final Completer aliasCompleter;
    private final Completer binaryCompleter;
    private final Completer filesCompleter;
    private final Completer directoriesCompleter;
    private final Environment env;

    public static CandidateCollector of(CommandRegistry registry, Environment env) {
        return new CandidateCollector(
                new StringsCompleter(registry.getBuiltinNames()),
                new StringsCompleter(env.getAliasManager().getAll().keySet()),
                new SystemBinaryCompleter(env),
                new Completers.FilesCompleter(env.getCurrentDir()),
                new Completers.DirectoriesCompleter(env.getCurrentDir()),
                env
        );
    }

    public List<Candidate> collect(LineReader reader, ParsedLine pl) {
        List<Candidate> candidates = new ArrayList<>();
        if (pl.wordIndex() == 0) {
            builtinCompleter.complete(reader, pl, candidates);
            aliasCompleter.complete(reader, pl, candidates);
            binaryCompleter.complete(reader, pl, candidates);
        } else {
            String command = pl.words().get(0);
            Optional<String> script = env.getCompletion(command);
            if (script.isPresent()) {
                runScript(script.get(), command, reader, pl, candidates);
            } else {
                filesCompleter.complete(reader, pl, candidates);
                directoriesCompleter.complete(reader, pl, candidates);
            }
        }
        return candidates;
    }

    private void runScript(String scriptPath, String command, LineReader reader, ParsedLine pl, List<Candidate> candidates) {
        String word = pl.word();
        String preceding = pl.wordIndex() >= 1 ? pl.words().get(pl.wordIndex() - 1) : "";

        try {
            ProcessBuilder pb = new ProcessBuilder(scriptPath, command, word, preceding);
            pb.environment().put("COMP_LINE", pl.line());
            pb.environment().put("COMP_POINT", String.valueOf(pl.cursor()));
            pb.directory(env.getCurrentDir());
            pb.redirectErrorStream(true);

            Process process = pb.start();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                br.lines()
                        .filter(line -> !line.isBlank())
                        .map(Candidate::new)
                        .forEach(candidates::add);
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            filesCompleter.complete(reader, pl, candidates);
        }
    }

}
