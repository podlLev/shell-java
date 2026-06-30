package command.builtin;

public final class EchoBuiltin implements Builtin {

    public static final EchoBuiltin INSTANCE = new EchoBuiltin();

    private EchoBuiltin() {}

    @Override
    public String name() {
        return "echo";
    }

    @Override
    public void execute(String arg) {
        System.out.println(arg);
    }

}
