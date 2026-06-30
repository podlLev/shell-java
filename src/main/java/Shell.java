import command.CommandRegistry;
import util.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Shell {

    private final Environment env;
    private final CommandRegistry registry;

    public Shell() {
        this.env = new Environment();
        this.registry = new CommandRegistry(env);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] parts = input.split(" ");
            String command = parts[0];
            List<String> arguments = Arrays.stream(parts).skip(1).toList();

            registry.execute(command, arguments);
        }
    }

}

