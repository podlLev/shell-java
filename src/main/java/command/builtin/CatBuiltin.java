package command.builtin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import redirect.OutputWriter;
import redirect.Redirect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while ((line = br.readLine()) != null) {
                    OutputWriter.write(line, redirect);
                }
            } catch (IOException e) {
                OutputWriter.writeError("cat: " + e.getMessage(), redirect);
            }
            return;
        }

        for (String path : args) {
            try (Stream<String> lines = Files.lines(Paths.get(path))) {
                lines.forEach(line -> OutputWriter.write(line, redirect));
            } catch (IOException e) {
                OutputWriter.writeError(String.format("cat: %s: %s", path, e.getMessage()), redirect);
            }
        }
    }

}
