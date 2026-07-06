package command.builtin;

import lombok.RequiredArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;
import util.Environment;

import java.util.List;

@RequiredArgsConstructor
public final class FcBuiltin implements Builtin {

    private final Environment env;

    @Override
    public String name() {
        return "fc";
    }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        List<String> entries = env.getHistoryManager().getEntries();
        if (entries.isEmpty()) return;

        if (!args.isEmpty() && args.get(0).equals("-l")) {
            handleList(args.subList(1, args.size()), entries, redirect);
            return;
        }

        if (!args.isEmpty()) {
            handleRerun(args.get(0), entries);
            return;
        }

        String last = entries.get(entries.size() - 1);
        System.out.println(last);
        env.getHistoryManager().add(last);
        OutputWriter.write(last, redirect);
    }

    private void handleList(List<String> args, List<String> entries, Redirect redirect) {
        int from = 1;
        int to = entries.size();

        if (!args.isEmpty()) {
            try { from = Integer.parseInt(args.get(0)); } catch (NumberFormatException ignored) {}
        }
        if (args.size() >= 2) {
            try { to = Integer.parseInt(args.get(1)); } catch (NumberFormatException ignored) {}
        }

        from = Math.max(1, from);
        to = Math.min(entries.size(), to);

        for (int i = from; i <= to; i++) {
            OutputWriter.write(String.format("%5d  %s", i, entries.get(i - 1)), redirect);
        }
    }

    private void handleRerun(String arg, List<String> entries) {
        try {
            int n = Integer.parseInt(arg);
            if (n >= 1 && n <= entries.size()) {
                String cmd = entries.get(n - 1);
                System.out.println(cmd);
            }
        } catch (NumberFormatException e) {
            System.err.printf("fc: %s: invalid option%n", arg);
        }
    }

}
