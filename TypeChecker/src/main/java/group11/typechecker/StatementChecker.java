package group11.typechecker;

import CPP.Absyn.*;

class StatementChecker implements Stm.Visitor<Object, Environment> {
    @Override
    public Object visit(SExp stm, Environment env) {
        ExpressionChecker.inferExpression(stm.exp_, env);
        return null;
    }

    @Override
    public Object visit(SDecls stm, Environment env) {
        for (String id : stm.listid_) {
            env.addVariable(id, TypeCoder.fromType(stm.type_));
        }
        return null;
    }

    @Override
    public Object visit(SInit stm, Environment env) {
        TypeCode t = TypeCoder.fromType(stm.type_);
        env.addVariable(stm.id_, t);
        ExpressionChecker.checkExpression(stm.exp_, t, env);
        return null;
    }

    @Override
    public Object visit(SReturn stm, Environment env) {
        TypeCode et = ExpressionChecker.inferExpression(stm.exp_, env);
        TypeCode t = env.getReturnType();
        if (et != t) {
            throw TypeException.returnType(stm, et, env);
        }
        return null;
    }

    @Override
    public Object visit(SReturnVoid stm, Environment env) {
        TypeCode et = TypeCode.VOID;
        TypeCode t = env.getReturnType();
        if (et != t) {
            throw TypeException.returnType(stm, et, env);
        }
        return null;
    }

    @Override
    public Object visit(SWhile stm, Environment env) {
        TypeCode et = ExpressionChecker.inferExpression(stm.exp_, env);
        TypeCode t = TypeCode.BOOL;
        if (et != t) {
            throw TypeException.expressionCondition(stm.exp_, et, env);
        }

        stm.stm_.accept(this, env);
        return null;
    }

    @Override
    public Object visit(SBlock stm, Environment env) {
        env.enterScope();
        for (Stm st : stm.liststm_) {
            st.accept(this, env);
        }
        env.leaveScope();
        return null;
    }

    @Override
    public Object visit(SIfElse stm, Environment env) {
        TypeCode et = ExpressionChecker.inferExpression(stm.exp_, env);
        TypeCode t = TypeCode.BOOL;
        if (et != t) {
            throw TypeException.expressionCondition(stm.exp_, et, env);
        }

        stm.stm_1.accept(this, env);
        stm.stm_2.accept(this, env);
        return null;
    }
}
