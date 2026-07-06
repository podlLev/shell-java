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
        env.getHistoryManager().load();
    }

    public void run() {
        registerShutdownHook();
        try (Terminal terminal = TerminalBuilder.builder().system(true).build()) {
            LineReader reader = buildReader(terminal);
            while (true) {
                env.getJobManager().reap();
                try {
                    String input = reader.readLine("$ ").trim();
                    if (!input.isEmpty()) handleInput(input);
                } catch (UnterminatedQuoteException e) {
                    System.err.println(e.getMessage());
                } catch (UserInterruptException e) {
                    // Ctrl+C
                } catch (EndOfFileException e) {
                    return;
                }
            }
        } catch (IOException e) {
            System.err.printf("terminal error: %s%n", e.getMessage());
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(
                new Thread(env.getHistoryManager()::save)
        );
    }

    private LineReader buildReader(Terminal terminal) {
        CandidateCollector collector = CandidateCollector.of(registry, env);

        DefaultParser parser = new DefaultParser();
        parser.setEscapeChars(null);

        return LineReaderBuilder.builder()
                .terminal(terminal)
                .completer((reader, line, candidates) ->
                        candidates.addAll(collector.collect(reader, line)))
                .parser(parser)
                .option(LineReader.Option.AUTO_MENU, true)
                .option(LineReader.Option.AUTO_LIST, true)
                .option(LineReader.Option.MENU_COMPLETE, true)
                .build();
    }

    private void handleInput(String input) {
        env.getHistoryManager().add(input);

        ParseResult result = tokenizer.tokenize(input);
        List<String> tokens = result.tokens();
        if (tokens.isEmpty()) return;

        String command = tokens.get(0);
        List<String> argTokens = tokens.subList(1, tokens.size());

        registry.execute(command, argTokens, result.redirect(), result.background());
    }

}

