package CPP.Absyn; // Java Package generated by the BNF Converter.

public class SReturnVoid extends Stm {
    public SReturnVoid() {
    }

    public <R, A> R accept(CPP.Absyn.Stm.Visitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof SReturnVoid;
    }

    public int hashCode() {
        return 37;
    }


}
