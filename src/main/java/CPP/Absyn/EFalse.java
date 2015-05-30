package CPP.Absyn; // Java Package generated by the BNF Converter.

public class EFalse extends Exp {
    public EFalse() {
    }

    public <R, A> R accept(CPP.Absyn.Exp.Visitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof CPP.Absyn.EFalse) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return 37;
    }


}
