import command.CommandRegistry;

import java.util.Scanner;

public class Shell {

    private final CommandRegistry registry;

    public Shell() {
        this.registry = new CommandRegistry();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] parts = input.split(" ", 2);
            String command = parts[0];
            String argument = parts.length > 1 ? parts[1] : "";

            registry.execute(command, argument);
        }
    }

}

