package command.builtin;

import redirect.Redirect;

import java.util.List;

public final class CompleteBuiltin implements Builtin {

    public static final CompleteBuiltin INSTANCE = new CompleteBuiltin();

    @Override
    public String name() {
        return "complete";
    }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        if (!args.isEmpty() && args.get(0).equals("-p")) {
            if (args.size() < 2) {
                return;
            }
            String target = args.get(1);
            System.out.println("complete: " + target + ": no completion specification");
        }
    }

}
