package redirect;

public record Redirect(RedirectType type, String filePath) {

    public Redirect {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("redirect target cannot be blank");
        }
    }

}
