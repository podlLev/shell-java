import command.CommandRegistry;
import util.Environment;
import util.ParseResult;
import util.Tokenizer;
import util.UnterminatedQuoteException;

import java.util.List;
import java.util.Scanner;

public class Shell {

    private final CommandRegistry registry;
    private final Tokenizer tokenizer;

    public Shell() {
        Environment env = new Environment();
        this.registry = new CommandRegistry(env);
        this.tokenizer = new Tokenizer();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            System.out.flush();
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            try {
                ParseResult result = tokenizer.tokenize(input);
                List<String> tokens = result.tokens();
                if (tokens.isEmpty()) continue;

                String command = tokens.get(0);
                List<String> argTokens = tokens.subList(1, tokens.size());

                registry.execute(command, argTokens, result.redirect());
            } catch (UnterminatedQuoteException e) {
                System.err.println(e.getMessage());
            }
        }
    }

}

