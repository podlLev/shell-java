package util;

import redirect.Redirect;
import redirect.RedirectType;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private static final char WHITESPACE = ' ';
    private static final char SINGLE = '\'';
    private static final char DOUBLE = '\"';
    private static final char TAB = '\t';
    private static final char BACKSLASH = '\\';
    private static final char DOLLAR = '$';
    private static final char BACKTICK = '`';

    public ParseResult tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);

            switch (c) {
                case SINGLE -> {
                    quoted = true;
                    i = parseSingleQuote(input, i + 1, current);
                }
                case DOUBLE -> {
                    quoted = true;
                    i = parseDoubleQuote(input, i + 1, current);
                }
                case BACKSLASH -> i = parseBackslash(input, i + 1, current);
                case WHITESPACE, TAB -> {
                    if (!current.isEmpty() || quoted) {
                        tokens.add(current.toString());
                        current.setLength(0);
                        quoted = false;
                    }
                    ++i;
                }
                default -> {
                    current.append(c);
                    ++i;
                }
            }
        }

        if (!current.isEmpty() || quoted) {
            tokens.add(current.toString());
        }
        return extractRedirect(tokens);
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
                default -> null;
            };

            if (type != null) {
                int fileIndex = i + 1;
                if (fileIndex >= tokens.size()) {
                    System.out.println("syntax error: expected filename after redirect");
                    return new ParseResult(tokens, null);
                }
                List<String> args = new ArrayList<>(tokens.subList(0, i));
                args.addAll(tokens.subList(fileIndex + 1, tokens.size()));
                return new ParseResult(args, new Redirect(type, tokens.get(fileIndex)));
            }
        }
        return new ParseResult(tokens, null);
    }

}
