package CPP;

public class TypeException extends RuntimeException {
    private final Env env;

    public TypeException(String msg) {
        super(msg);
        this.env = null;
    }

    public TypeException(String msg, Env env) {
        super(msg);
        this.env = env;
    }

    public Env getEnv() {
        return env;
    }
}
