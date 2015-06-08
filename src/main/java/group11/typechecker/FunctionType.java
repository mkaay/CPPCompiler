package group11.typechecker;

import java.util.List;

public class FunctionType {
    public List<TypeCode> args;
    public TypeCode val;

    public FunctionType(List<TypeCode> args, TypeCode val) {
        this.args = args;
        this.val = val;
    }
}
