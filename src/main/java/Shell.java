import command.CommandRegistry;
import completer.CandidateCollector;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import pipe.Pipeline;
import pipe.PipelineResult;
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
        this.tokenizer = new Tokenizer(env);
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
        if (input.matches("[A-Za-z_][A-Za-z0-9_]*=.*")) {
            int eq = input.indexOf('=');
            env.setVariable(input.substring(0, eq), input.substring(eq + 1));
            return;
        }

        String expanded = expandHistory(input);
        if (expanded == null) return;
        if (!expanded.equals(input)) System.out.println(expanded);

        env.getHistoryManager().add(expanded);

        PipelineResult pipeline = tokenizer.tokenize(expanded);
        new Pipeline(registry, env).execute(pipeline);
    }

    private String expandHistory(String input) {
        List<String> entries = env.getHistoryManager().getEntries();

        if (input.equals("!!")) {
            if (entries.isEmpty()) {
                System.err.println("!!: event not found");
                return null;
            }
            return entries.get(entries.size() - 1);
        }

        if (input.startsWith("!")) {
            String rest = input.substring(1);
            try {
                int n = Integer.parseInt(rest);
                if (n < 1 || n > entries.size()) {
                    System.err.printf("!%d: event not found%n", n);
                    return null;
                }
                return entries.get(n - 1);
            } catch (NumberFormatException e) {
                for (int i = entries.size() - 1; i >= 0; i--) {
                    if (entries.get(i).startsWith(rest)) {
                        return entries.get(i);
                    }
                }
                System.err.printf("!%s: event not found%n", rest);
                return null;
            }
        }
        return input;
    }

}

