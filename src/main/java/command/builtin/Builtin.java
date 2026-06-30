package command.builtin;

public interface Builtin {

    String name();
    void execute(String args);

}
