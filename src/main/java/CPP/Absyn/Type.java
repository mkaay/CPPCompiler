package CPP.Absyn; // Java Package generated by the BNF Converter.

public abstract class Type implements java.io.Serializable {
    public abstract <R, A> R accept(Type.Visitor<R, A> v, A arg);

    public interface Visitor<R, A> {
        R visit(CPP.Absyn.Type_bool p, A arg);

        R visit(CPP.Absyn.Type_int p, A arg);

        R visit(CPP.Absyn.Type_double p, A arg);

        R visit(CPP.Absyn.Type_void p, A arg);

        R visit(CPP.Absyn.Type_string p, A arg);

    }

}
