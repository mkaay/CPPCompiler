package group11.typechecker;

import CPP.Absyn.*;

class TypeCoder implements Type.Visitor<TypeCode, Object> {

    public static TypeCode fromType(Type t) {
        return t.accept(new TypeCoder(), null);
    }

    @Override
    public TypeCode visit(Type_bool p, Object arg) {
        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(Type_int p, Object arg) {
        return TypeCode.INT;
    }

    @Override
    public TypeCode visit(Type_double p, Object arg) {
        return TypeCode.DOUBLE;
    }

    @Override
    public TypeCode visit(Type_void p, Object arg) {
        return TypeCode.VOID;
    }

    @Override
    public TypeCode visit(Type_string p, Object arg) {
        return TypeCode.STRING;
    }
}
