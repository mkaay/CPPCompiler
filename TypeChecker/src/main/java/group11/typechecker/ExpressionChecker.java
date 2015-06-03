package group11.typechecker;

import CPP.Absyn.*;

public class ExpressionChecker implements Exp.Visitor<TypeCode, Environment> {
    public static TypeCode inferExpression(Exp e, Environment env) {
        return e.accept(new ExpressionChecker(), env);
    }

    public static void checkExpression(Exp e, TypeCode t, Environment env) {
        TypeCode et = inferExpression(e, env);
        if (et != t) {
            throw TypeException.expressionType(e, et, t, env);
        }
    }

    public static TypeCode checkExpressionIsNumber(Exp e, Environment env) {
        TypeCode et = inferExpression(e, env);

        if (!et.equals(TypeCode.INT) && !et.equals(TypeCode.DOUBLE)) {
            throw TypeException.expressionNumber(e, et, env);
        }

        return et;
    }

    @Override
    public TypeCode visit(ETrue p, Environment env) {
        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EFalse p, Environment env) {
        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EInt p, Environment env) {
        return TypeCode.INT;
    }

    @Override
    public TypeCode visit(EDouble p, Environment env) {
        return TypeCode.DOUBLE;
    }

    @Override
    public TypeCode visit(EString p, Environment env) {
        return TypeCode.STRING;
    }

    @Override
    public TypeCode visit(EId p, Environment env) {
        return env.lookupVariable(p.id_);
    }

    @Override
    public TypeCode visit(EApp p, Environment env) {
        FunctionType ft = env.lookupFunction(p.id_);

        if (p.listexp_.size() != ft.args.size()) {
            throw TypeException.argumentCount(p, p.id_, p.listexp_.size(), ft.args.size(), env);
        }

        int i = 0;
        for (Exp e : p.listexp_) {
            checkExpression(e, ft.args.get(i), env);
            i++;
        }

        return ft.val;
    }

    @Override
    public TypeCode visit(EPIncr p, Environment env) {
        return checkExpressionIsNumber(p.exp_, env);
    }

    @Override
    public TypeCode visit(EPDecr p, Environment env) {
        return checkExpressionIsNumber(p.exp_, env);
    }

    @Override
    public TypeCode visit(EIncr p, Environment env) {
        return checkExpressionIsNumber(p.exp_, env);
    }

    @Override
    public TypeCode visit(EDecr p, Environment env) {
        return checkExpressionIsNumber(p.exp_, env);
    }

    @Override
    public TypeCode visit(ETimes p, Environment env) {
        TypeCode et = checkExpressionIsNumber(p.exp_1, env);

        checkExpression(p.exp_2, et, env);

        return et;
    }

    @Override
    public TypeCode visit(EDiv p, Environment env) {
        TypeCode et = checkExpressionIsNumber(p.exp_1, env);

        checkExpression(p.exp_2, et, env);

        return et;
    }

    @Override
    public TypeCode visit(EPlus p, Environment env) {
        TypeCode et = checkExpressionIsNumber(p.exp_1, env);

        checkExpression(p.exp_2, et, env);

        return et;
    }

    @Override
    public TypeCode visit(EMinus p, Environment env) {
        TypeCode et = checkExpressionIsNumber(p.exp_1, env);

        checkExpression(p.exp_2, et, env);

        return et;
    }

    @Override
    public TypeCode visit(ELt p, Environment env) {
        TypeCode et = checkExpressionIsNumber(p.exp_1, env);

        checkExpression(p.exp_2, et, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EGt p, Environment env) {
        TypeCode et = checkExpressionIsNumber(p.exp_1, env);

        checkExpression(p.exp_2, et, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(ELtEq p, Environment env) {
        TypeCode et = checkExpressionIsNumber(p.exp_1, env);

        checkExpression(p.exp_2, et, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EGtEq p, Environment env) {
        TypeCode et = checkExpressionIsNumber(p.exp_1, env);

        checkExpression(p.exp_2, et, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EEq p, Environment env) {
        TypeCode et = inferExpression(p.exp_1, env);

        checkExpression(p.exp_2, et, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(ENEq p, Environment env) {
        TypeCode et = inferExpression(p.exp_1, env);

        checkExpression(p.exp_2, et, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EAnd p, Environment env) {
        checkExpression(p.exp_1, TypeCode.BOOL, env);
        checkExpression(p.exp_2, TypeCode.BOOL, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EOr p, Environment env) {
        checkExpression(p.exp_1, TypeCode.BOOL, env);
        checkExpression(p.exp_2, TypeCode.BOOL, env);

        return TypeCode.BOOL;
    }

    @Override
    public TypeCode visit(EAss p, Environment env) {
        TypeCode et = inferExpression(p.exp_1, env);

        checkExpression(p.exp_2, et, env);

        return et;
    }

    @Override
    public TypeCode visit(ETyped p, Environment env) {
        return inferExpression(p.exp_, env);
    }

}
