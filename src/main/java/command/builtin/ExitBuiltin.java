package command.builtin;

public enum ExitBuiltin implements Builtin {

    INSTANCE;

    @Override
    public void execute(String args) {
        System.exit(0);
    }

}
