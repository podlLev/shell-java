package command.builtin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CatBuiltin implements Builtin {

    public static final CatBuiltin INSTANCE = new CatBuiltin();

    @Override
    public String name() { return "cat"; }

    @Override
    public void execute(String command, List<String> args, Redirect redirect) {
        if (args.isEmpty()) {
            System.out.println("cat: missing operand");
            return;
        }

        for (String path : args) {
            try (Stream<String> lines = Files.lines(Paths.get(path))) {
                lines.forEach(line -> OutputWriter.write(line, redirect));
            } catch (IOException e) {
                System.out.printf("cat: %s: %s%n", path, e.getMessage());
            }
        }
    }

}
