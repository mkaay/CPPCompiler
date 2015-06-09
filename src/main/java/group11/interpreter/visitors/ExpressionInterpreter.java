package group11.interpreter.visitors;

import CPP.Absyn.*;
import group11.interpreter.helpers.FunctionScope;
import org.robovm.llvm.binding.ValueRef;

public class ExpressionInterpreter implements CPP.Absyn.Exp.Visitor<ValueRef, FunctionScope> {
    @Override
    public ValueRef visit(ETrue exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EFalse exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EInt exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EDouble exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EString exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EId exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EApp exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EPIncr exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EPDecr exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EIncr exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EDecr exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(ETimes exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EDiv exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EPlus exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EMinus exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(ELt exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EGt exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(ELtEq exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EGtEq exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EEq exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(ENEq exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EAnd exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EOr exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(EAss exp, FunctionScope functionScope) {
        return null;
    }

    @Override
    public ValueRef visit(ETyped exp, FunctionScope functionScope) {
        return null;
    }
}
