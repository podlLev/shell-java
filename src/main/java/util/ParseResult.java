package util;

import redirect.Redirect;

import java.util.List;

public record ParseResult(List<String> tokens, Redirect redirect) {}
