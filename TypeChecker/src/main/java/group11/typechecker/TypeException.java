package group11.typechecker;

import CPP.Absyn.Exp;
import CPP.Absyn.Stm;
import CPP.PrettyPrinter;

public class TypeException extends RuntimeException {
    private TypeException(String msg) {
        super(msg);
    }

    public static TypeException returnType(Stm stm, TypeCode actualType, Env env) {
        return new TypeException(String.format("Function '%s' returns type '%s' expected '%s', at '%s'",
                env.getFunctionName(), actualType, env.getReturnType(), PrettyPrinter.print(stm)));
    }

    public static TypeException expressionCondition(Exp exp, TypeCode actualType, Env env) {
        return new TypeException(String.format("Expression '%s' has type '%s' expected '%s', in function '%s'",
                PrettyPrinter.print(exp), actualType, TypeCode.BOOL, env.getFunctionName()));
    }

    public static TypeException expressionType(Exp exp, TypeCode actualType, TypeCode expectedType, Env env) {
        return new TypeException(String.format("Expression '%s' has type '%s' expected '%s', in function '%s'",
                PrettyPrinter.print(exp), actualType, expectedType, env.getFunctionName()));
    }

    public static TypeException expressionNumber(Exp exp, TypeCode actualType, Env env) {
        return new TypeException(String.format("Expression '%s' has type '%s' expected 'int' or 'double', in function '%s'",
                PrettyPrinter.print(exp), actualType, env.getFunctionName()));
    }

    public static TypeException argumentCount(Exp exp, String name, int actualCount, int expectedCount, Env env) {
        return new TypeException(String.format("Function '%s' expects %d arguments, got %d instead, at %s in function '%s'",
                name, expectedCount, actualCount, PrettyPrinter.print(exp), env.getFunctionName()));
    }

    public static TypeException variableUnknown(String name, Env env) {
        return new TypeException(String.format("Variable '%s' not declared in this scope", name));
    }

    public static TypeException variableExists(String name, Env env) {
        return new TypeException(String.format("Variable '%s' is already declared in this scope", name));
    }

    public static TypeException functionUnknown(String name, Env env) {
        return new TypeException(String.format("Function '%s' does not exist", name));
    }

    public static TypeException functionExists(String name, Env env) {
        return new TypeException(String.format("Function '%s' already exist", name));
    }
}
