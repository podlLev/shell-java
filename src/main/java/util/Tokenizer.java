package util;

import lombok.RequiredArgsConstructor;
import pipe.PipelineResult;
import redirect.Redirect;
import redirect.RedirectType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Tokenizer {

    private static final char WHITESPACE = ' ';
    private static final char SINGLE = '\'';
    private static final char DOUBLE = '\"';
    private static final char TAB = '\t';
    private static final char BACKSLASH = '\\';
    private static final char DOLLAR = '$';
    private static final char BACKTICK = '`';

    private final Environment env;

    public PipelineResult tokenize(String input) {
        List<String> stages = splitByPipe(input);

        boolean background = false;
        if (!stages.isEmpty()) {
            String last = stages.get(stages.size() - 1);
            if (last.endsWith("&")) {
                background = true;
                stages.set(stages.size() - 1, last.substring(0, last.length() - 1).trim());
            }
        }

        List<ParseResult> parsed = stages.stream()
                .map(this::tokenizeStage)
                .collect(Collectors.toList());

        return new PipelineResult(parsed, background);
    }

    private List<String> splitByPipe(String input) {
        List<String> stages = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingle = false;
        boolean inDouble = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == SINGLE && !inDouble) inSingle = !inSingle;
            else if (c == DOUBLE && !inSingle) inDouble = !inDouble;
            else if (c == '|' && !inSingle && !inDouble) {
                stages.add(current.toString().trim());
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        if (!current.isEmpty()) stages.add(current.toString().trim());
        return stages;
    }

    private ParseResult tokenizeStage(String input) {
        input = expandVariables(input);
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);
            switch (c) {
                case SINGLE -> { quoted = true; i = parseSingleQuote(input, i + 1, current); }
                case DOUBLE -> { quoted = true; i = parseDoubleQuote(input, i + 1, current); }
                case BACKSLASH -> i = parseBackslash(input, i + 1, current);
                case WHITESPACE, TAB -> {
                    if (!current.isEmpty() || quoted) {
                        tokens.add(current.toString());
                        current.setLength(0);
                        quoted = false;
                    }
                    i++;
                }
                default -> { current.append(c); i++; }
            }
        }
        if (!current.isEmpty() || quoted) tokens.add(current.toString());
        return extractRedirect(tokens);
    }

    private String expandVariables(String input) {
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);

            if (c == SINGLE) {
                result.append(c);
                i++;
                while (i < input.length() && input.charAt(i) != SINGLE) {
                    result.append(input.charAt(i++));
                }
                if (i < input.length()) result.append(input.charAt(i++));
                continue;
            }

            if (c == '$' && i + 1 < input.length()) {
                i++;
                char next = input.charAt(i);

                if (next == '{') {
                    int end = input.indexOf('}', i + 1);
                    if (end != -1) {
                        String name = input.substring(i + 1, end);
                        result.append(resolveVariable(name));
                        i = end + 1;
                        continue;
                    }
                } else if (next == '?') {
                    result.append(env.getLastExitCode());
                    i++;
                    continue;
                } else if (next == '$') {
                    result.append(ProcessHandle.current().pid());
                    i++;
                    continue;
                } else if (next == '!') {
                    result.append(env.getLastBackgroundPid());
                    i++;
                    continue;
                } else if (Character.isLetterOrDigit(next) || next == '_') {
                    int start = i;
                    while (i < input.length() &&
                            (Character.isLetterOrDigit(input.charAt(i)) || input.charAt(i) == '_')) {
                        i++;
                    }
                    String name = input.substring(start, i);
                    result.append(resolveVariable(name));
                    continue;
                }
                result.append('$');
                continue;
            }
            result.append(c);
            i++;
        }
        return result.toString();
    }

    private String resolveVariable(String name) {
        return env.getVariable(name).orElse("");
    }

    private int parseSingleQuote(String input, int i, StringBuilder current) {
        while (i < input.length()) {
            char c = input.charAt(i++);
            if (c == SINGLE) {
                return i;
            }
            current.append(c);
        }
        throw new UnterminatedQuoteException(SINGLE);
    }

    private int parseDoubleQuote(String input, int i, StringBuilder current) {
        while (i < input.length()) {
            char c = input.charAt(i++);
            if (c == DOUBLE) return i;
            if (c == BACKSLASH && i < input.length()) {
                char next = input.charAt(i++);
                if (next == BACKSLASH || next == DOUBLE || next == DOLLAR || next == BACKTICK) {
                    current.append(next);
                    continue;
                }
                current.append(BACKSLASH);
                current.append(next);
            } else {
                current.append(c);
            }
        }
        throw new UnterminatedQuoteException(DOUBLE);
    }

    private int parseBackslash(String input, int i, StringBuilder current) {
        if (i < input.length()) {
            current.append(input.charAt(i));
            return i + 1;
        }
        return i;
    }

    private ParseResult extractRedirect(List<String> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            RedirectType type = switch (token) {
                case ">", "1>" -> RedirectType.STDOUT;
                case "2>" -> RedirectType.STDERR;
                case ">>", "1>>" -> RedirectType.STDOUT_APPEND;
                case "2>>" -> RedirectType.STDERR_APPEND;
                default -> null;
            };

            if (type != null) {
                int fileIndex = i + 1;
                if (fileIndex >= tokens.size()) {
                    System.err.println("syntax error: expected filename after redirect");
                    return new ParseResult(tokens, null, false);
                }
                List<String> args = new ArrayList<>(tokens.subList(0, i));
                args.addAll(tokens.subList(fileIndex + 1, tokens.size()));
                return new ParseResult(args, new Redirect(type, tokens.get(fileIndex)), false);
            }
        }
        return new ParseResult(tokens, null, false);
    }

}
