package group11.typechecker;

import CPP.Absyn.*;

public class TypeInferrer implements Exp.Visitor<TypeCode, Env> {
    public static TypeCode inferExp(Exp e, Env env) {
        return e.accept(new TypeInferrer(), env);
    }

    public static void checkExp(Exp e, TypeCode t, Env env) {
        TypeCode et = inferExp(e, env);
        if (et != t) {
            throw TypeException.expressionType(e, et, t, env);
        }
    }

    public static TypeCode checkNumber(Exp e, Env env) {
        TypeCode et = inferExp(e, env);

        if (!et.equals(TypeCode.INT) && !et.equals(TypeCode.DOUBLE)) {
            throw TypeException.expressionNumber(e, et, env);
        }

        return et;
    }

    @Override
    public TypeCode visit(ETrue p, Env env) {
        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EFalse p, Env env) {
        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EInt p, Env env) {
        return TypeCode.INT;
    }

    @Override
    public TypeCode visit(EDouble p, Env env) {
        return TypeCode.DOUBLE;
    }

    @Override
    public TypeCode visit(EString p, Env env) {
        return TypeCode.STRING;
    }

    @Override
    public TypeCode visit(EId p, Env env) {
        return env.lookupVar(p.id_);
    }

    @Override
    public TypeCode visit(EApp p, Env env) {
        FunType ft = env.lookupFun(p.id_);

        if (p.listexp_.size() != ft.args.size()) {
            throw TypeException.argumentCount(p, p.id_, p.listexp_.size(), ft.args.size(), env);
        }

        int i = 0;
        for (Exp e : p.listexp_) {
            checkExp(e, ft.args.get(i), env);
            i++;
        }

        return ft.val;
    }

    @Override
    public TypeCode visit(EPIncr p, Env env) {
        return checkNumber(p.exp_, env);
    }

    @Override
    public TypeCode visit(EPDecr p, Env env) {
        return checkNumber(p.exp_, env);
    }

    @Override
    public TypeCode visit(EIncr p, Env env) {
        return checkNumber(p.exp_, env);
    }

    @Override
    public TypeCode visit(EDecr p, Env env) {
        return checkNumber(p.exp_, env);
    }

    @Override
    public TypeCode visit(ETimes p, Env env) {
        TypeCode et = checkNumber(p.exp_1, env);

        checkExp(p.exp_2, et, env);

        return et;
    }

    @Override
    public TypeCode visit(EDiv p, Env env) {
        TypeCode et = checkNumber(p.exp_1, env);

        checkExp(p.exp_2, et, env);

        return et;
    }

    @Override
    public TypeCode visit(EPlus p, Env env) {
        TypeCode et = checkNumber(p.exp_1, env);

        checkExp(p.exp_2, et, env);

        return et;
    }

    @Override
    public TypeCode visit(EMinus p, Env env) {
        TypeCode et = checkNumber(p.exp_1, env);

        checkExp(p.exp_2, et, env);

        return et;
    }

    @Override
    public TypeCode visit(ELt p, Env env) {
        TypeCode et = checkNumber(p.exp_1, env);

        checkExp(p.exp_2, et, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EGt p, Env env) {
        TypeCode et = checkNumber(p.exp_1, env);

        checkExp(p.exp_2, et, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(ELtEq p, Env env) {
        TypeCode et = checkNumber(p.exp_1, env);

        checkExp(p.exp_2, et, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EGtEq p, Env env) {
        TypeCode et = checkNumber(p.exp_1, env);

        checkExp(p.exp_2, et, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EEq p, Env env) {
        TypeCode et = inferExp(p.exp_1, env);

        checkExp(p.exp_2, et, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(ENEq p, Env env) {
        TypeCode et = inferExp(p.exp_1, env);

        checkExp(p.exp_2, et, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EAnd p, Env env) {
        checkExp(p.exp_1, TypeCode.BOOL, env);
        checkExp(p.exp_2, TypeCode.BOOL, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EOr p, Env env) {
        checkExp(p.exp_1, TypeCode.BOOL, env);
        checkExp(p.exp_2, TypeCode.BOOL, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EAss p, Env env) {
        TypeCode et = inferExp(p.exp_1, env);

        checkExp(p.exp_2, et, env);

        return et;
    }

    @Override
    public TypeCode visit(ETyped p, Env env) {
        return inferExp(p.exp_, env);
    }

}
