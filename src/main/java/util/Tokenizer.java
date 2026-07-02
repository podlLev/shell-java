package util;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private static final char WHITESPACE = ' ';
    private static final char SINGLE = '\'';
    private static final char DOUBLE = '\"';
    private static final char TAB = '\t';
    private static final char BACKSLASH = '\\';

    public List<String> tokenize(String input) {
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
        return tokens;
    }

    private int parseSingleQuote(String input, int i, StringBuilder current) {
        while (i < input.length()) {
            char c = input.charAt(i++);
            if (c == SINGLE) {
                return i;
            }
            current.append(c);
        }
        return i;
    }

    private int parseDoubleQuote(String input, int i, StringBuilder current) {
        while (i < input.length()) {
            char c = input.charAt(i++);
            if (c == DOUBLE) {
                return i;
            } else if (c == BACKSLASH && i < input.length()) {
                char next = input.charAt(i++);
                if (next == BACKSLASH || next == DOUBLE) {
                    current.append(next);
                    continue;
                }
            }
            current.append(c);
        }
        return i;
    }

    private int parseBackslash(String input, int i, StringBuilder current) {
        if (i < input.length()) {
            current.append(input.charAt(i));
            return i + 1;
        }
        return i;
    }

}
