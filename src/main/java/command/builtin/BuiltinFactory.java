package command.builtin;

import util.Environment;

import java.util.function.Function;

public enum BuiltinFactory {
    EXIT(ExitBuiltin.INSTANCE),
    ECHO(EchoBuiltin.INSTANCE),
    MKDIR(MkdirBuiltin.INSTANCE),
    RMDIR(RmdirBuiltin.INSTANCE),
    TOUCH(TouchBuiltin.INSTANCE),
    CAT(CatBuiltin.INSTANCE),
    CLEAR(ClearBuiltin.INSTANCE),

    PWD(PwdBuiltin::new),
    CD(CdBuiltin::new),
    LS(LsBuiltin::new),
    COMPLETE(CompleteBuiltin::new),
    JOBS(JobsBuiltin::new),
    HISTORY(HistoryBuiltin::new),
    FC(FcBuiltin::new),
    DECLARE(DeclareBuiltin::new),
    EXPORT(ExportBuiltin::new),
    UNSET(UnsetBuiltin::new);

    private final Function<Environment, Builtin> factory;

    BuiltinFactory(Builtin instance) {
        this.factory = env -> instance;
    }

    BuiltinFactory(Function<Environment, Builtin> factory) {
        this.factory = factory;
    }

    public Builtin create(Environment env) {
        return factory.apply(env);
    }

}
