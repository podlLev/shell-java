package command.builtin;

public enum EchoBuiltin implements Builtin {

    INSTANCE;

    @Override
    public void execute(String arg) {
        System.out.println(arg);
    }

}
