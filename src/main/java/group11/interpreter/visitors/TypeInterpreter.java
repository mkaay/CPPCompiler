package group11.interpreter.visitors;

import CPP.Absyn.*;
import org.robovm.llvm.binding.LLVM;
import org.robovm.llvm.binding.TypeRef;

public class TypeInterpreter implements Type.Visitor<TypeRef, Object> {
    @Override
    public TypeRef visit(Type_bool p, Object arg) {
        return LLVM.Int1Type();
    }

    @Override
    public TypeRef visit(Type_int p, Object arg) {
        return LLVM.Int32Type();
    }

    @Override
    public TypeRef visit(Type_double p, Object arg) {
        return LLVM.DoubleType();
    }

    @Override
    public TypeRef visit(Type_void p, Object arg) {
        return LLVM.VoidType();
    }

    @Override
    public TypeRef visit(Type_string p, Object arg) {
        return LLVM.PointerType(LLVM.Int8Type(), 0);
    }
}
