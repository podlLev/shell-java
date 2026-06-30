package command.builtin;

public final class ExitBuiltin implements Builtin {

    public static final ExitBuiltin INSTANCE = new ExitBuiltin();

    private ExitBuiltin() {}

    @Override
    public String name() {
        return "exit";
    }

    @Override
    public void execute(String args) {
        System.exit(0);
    }

}
