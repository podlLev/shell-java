import command.CommandRegistry;
import util.Environment;
import util.Tokenizer;

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
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            List<String> tokens = tokenizer.tokenize(input);
            if (tokens.isEmpty()) continue;

            String command = tokens.get(0);
            List<String> argTokens = tokens.subList(1, tokens.size());

            registry.execute(command, argTokens);
        }
    }

}

