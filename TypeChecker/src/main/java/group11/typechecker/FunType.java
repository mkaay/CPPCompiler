package group11.typechecker;

import java.util.List;

public class FunType {
    public List<TypeCode> args;
    public TypeCode val;

    public FunType(List<TypeCode> args, TypeCode val) {
        this.args = args;
        this.val = val;
    }
}
