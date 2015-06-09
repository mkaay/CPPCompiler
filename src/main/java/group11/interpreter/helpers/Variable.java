package group11.interpreter.helpers;

import org.robovm.llvm.binding.TypeRef;
import org.robovm.llvm.binding.ValueRef;

public class Variable {
    public final TypeRef type;
    public final ValueRef value;

    public Variable(TypeRef type, ValueRef value) {
        this.type = type;
        this.value = value;
    }
}
