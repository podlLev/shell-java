package pipe;

import command.CommandRegistry;
import lombok.RequiredArgsConstructor;
import redirect.Redirect;
import util.Environment;
import util.ParseResult;
import util.ProcessBuilderFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Pipeline {

    private final CommandRegistry registry;
    private final Environment env;

    public void execute(PipelineResult pipeline) {
        List<ParseResult> stages = pipeline.stages();

        if (stages.size() == 1) {
            ParseResult stage = stages.get(0);
            List<String> tokens = stage.tokens();
            if (tokens.isEmpty()) return;
            registry.execute(tokens.get(0), tokens.subList(1, tokens.size()),
                    stage.redirect(), pipeline.background());
            return;
        }
        executePipeline(stages, pipeline.background());
    }

    private void executePipeline(List<ParseResult> stages, boolean background) {
        int n = stages.size();
        List<Process> processes = new ArrayList<>();
        InputStream previousOutput = null;

        for (int i = 0; i < n; i++) {
            ParseResult stage = stages.get(i);
            List<String> tokens = stage.tokens();
            if (tokens.isEmpty()) continue;

            String command = tokens.get(0);
            List<String> args = tokens.subList(1, tokens.size());
            boolean isLast = (i == n - 1);

            if (registry.isBuiltin(command)) {
                previousOutput = executeBuiltinInPipeline(
                        command, args, stage.redirect(), previousOutput, isLast);
            } else {
                try {
                    ProcessBuilder pb = ProcessBuilderFactory.build(command, args, env);

                    pb.redirectInput(previousOutput != null
                            ? ProcessBuilder.Redirect.PIPE
                            : ProcessBuilder.Redirect.INHERIT);

                    if (!isLast) {
                        pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
                    } else {
                        ProcessBuilderFactory.configureOutput(pb, stage.redirect());
                    }

                    pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                    pb.directory(env.getCurrentDir());

                    Process process = pb.start();

                    if (previousOutput != null) {
                        final InputStream prev = previousOutput;
                        Thread feeder = new Thread(() -> {
                            try (OutputStream stdin = process.getOutputStream()) {
                                prev.transferTo(stdin);
                            } catch (IOException ignored) {}
                        });
                        feeder.setDaemon(true);
                        feeder.start();
                    }

                    processes.add(process);
                    previousOutput = process.getInputStream();

                } catch (IOException e) {
                    System.err.printf("%s: %s%n", command, e.getMessage());
                    return;
                }
            }
        }

        if (!background) {
            for (Process p : processes) {
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } else {
            for (int i = 0; i < processes.size(); i++) {
                String cmdLine = stages.get(i).tokens().get(0);
                env.getJobManager().addJob(processes.get(i), cmdLine);
            }
            System.out.printf("[%d] %d%n",
                    processes.size(),
                    processes.get(processes.size() - 1).pid());
        }
    }

    private InputStream executeBuiltinInPipeline(
            String command,
            List<String> args,
            Redirect redirect,
            InputStream input,
            boolean isLast) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;

        try {
            if (input != null) System.setIn(input);
            if (!isLast) System.setOut(new PrintStream(baos));
            registry.execute(command, args, redirect, false);
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
        }

        if (!isLast) {
            return new ByteArrayInputStream(baos.toByteArray());
        }
        return null;
    }

}
