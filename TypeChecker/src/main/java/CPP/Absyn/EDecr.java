package CPP.Absyn; // Java Package generated by the BNF Converter.

public class EDecr extends Exp {
    public final Exp exp_;

    public EDecr(Exp p1) {
        exp_ = p1;
    }

    public <R, A> R accept(CPP.Absyn.Exp.Visitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof CPP.Absyn.EDecr) {
            CPP.Absyn.EDecr x = (CPP.Absyn.EDecr) o;
            return this.exp_.equals(x.exp_);
        }
        return false;
    }

    public int hashCode() {
        return this.exp_.hashCode();
    }


}