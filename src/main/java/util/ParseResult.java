package util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redirect.Redirect;

import java.util.List;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class ParseResult {

    private final List<String> tokens;

    @Getter(AccessLevel.NONE)
    private final Redirect redirect;

    public Optional<Redirect> getRedirect() {
        return Optional.ofNullable(redirect);
    }

}
