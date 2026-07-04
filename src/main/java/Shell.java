import command.CommandRegistry;
import completer.CandidateCollector;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import util.*;

import java.io.IOException;
import java.util.List;

public class Shell {

    private final CommandRegistry registry;
    private final Environment env;
    private final Tokenizer tokenizer;

    public Shell() {
        this.env = new Environment();
        this.registry = new CommandRegistry(env);
        this.tokenizer = new Tokenizer();
    }

    public void run() {
        try (Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build()) {

            DefaultParser parser = new DefaultParser();
            parser.setEscapeChars(null);

            Completer completer = (reader, line, candidates) ->
                    candidates.addAll(CandidateCollector.of(registry, env).collect(reader, line));

            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(completer)
                    .parser(parser)
                    .option(LineReader.Option.AUTO_MENU, true)
                    .option(LineReader.Option.AUTO_LIST, true)
                    .option(LineReader.Option.MENU_COMPLETE, true)
                    .build();

            while (true) {
                try {
                    String input = reader.readLine("$ ").trim();
                    if (input.isEmpty()) continue;

                    ParseResult result = tokenizer.tokenize(input);
                    List<String> tokens = result.tokens();
                    if (tokens.isEmpty()) continue;

                    String command = tokens.get(0);
                    List<String> argTokens = tokens.subList(1, tokens.size());

                    registry.execute(command, argTokens, result.redirect());
                } catch (UnterminatedQuoteException e) {
                    System.err.println(e.getMessage());
                } catch (UserInterruptException e) {
                    // Ctrl+C — JLine clears current line, loop continues
                } catch (EndOfFileException e) {
                    // Ctrl+D — exit shell
                    return;
                }
            }
        } catch (IOException e) {
            System.err.printf("terminal error: %s%n", e.getMessage());
        }
    }

}

