package group11.interpreter.visitors;

import CPP.Absyn.*;
import group11.interpreter.helpers.FunctionScope;
import group11.interpreter.helpers.SmartValueRefArray;
import org.robovm.llvm.binding.IntPredicate;
import org.robovm.llvm.binding.LLVM;
import org.robovm.llvm.binding.ValueRef;

import java.math.BigInteger;

public class ExpressionInterpreter implements CPP.Absyn.Exp.Visitor<ValueRef, FunctionScope> {
    @Override
    public ValueRef visit(ETrue exp, FunctionScope functionScope) {
        return LLVM.ConstInt(LLVM.Int1Type(), BigInteger.ONE, false);
    }

    @Override
    public ValueRef visit(EFalse exp, FunctionScope functionScope) {
        return LLVM.ConstInt(LLVM.Int1Type(), BigInteger.ZERO, false);
    }

    @Override
    public ValueRef visit(EInt exp, FunctionScope functionScope) {
        return LLVM.ConstInt(LLVM.Int32Type(), BigInteger.valueOf(exp.integer_), false);
    }

    @Override
    public ValueRef visit(EDouble exp, FunctionScope functionScope) {
        return LLVM.ConstReal(LLVM.DoubleType(), exp.double_);
    }

    @Override
    public ValueRef visit(EString exp, FunctionScope functionScope) {
        return LLVM.ConstString(exp.string_, false);
    }

    @Override
    public ValueRef visit(EId exp, FunctionScope functionScope) {
        return functionScope.getVariable(exp.id_).value;
    }

    @Override
    public ValueRef visit(EApp exp, FunctionScope functionScope) {
        SmartValueRefArray args = new SmartValueRefArray(exp.listexp_.size());
        for (Exp arg : exp.listexp_) {
            args.add(arg.accept(new ExpressionInterpreter(), functionScope));
        }

        // void return functions can't have name
        return LLVM.BuildCall(functionScope.getBuilder(), FunctionScope.getFunction(exp.id_),
                args, exp.listexp_.size(), ""); //  functionScope.uniqueName(String.format("%s_ret", exp.id_))
    }

    @Override
    public ValueRef visit(EPIncr exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = LLVM.ConstInt(LLVM.Int32Type(), BigInteger.ONE, false);
        ValueRef ret = LLVM.BuildAdd(functionScope.getBuilder(), left, right, functionScope.uniqueName("pincr"));
        LLVM.BuildStore(functionScope.getBuilder(), ret, left);
        return ret;
    }

    @Override
    public ValueRef visit(EPDecr exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = LLVM.ConstInt(LLVM.Int32Type(), BigInteger.ONE, false);
        ValueRef ret = LLVM.BuildSub(functionScope.getBuilder(), left, right, functionScope.uniqueName("pdecr"));
        LLVM.BuildStore(functionScope.getBuilder(), ret, left);
        return ret;
    }

    @Override
    public ValueRef visit(EIncr exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = LLVM.ConstInt(LLVM.Int32Type(), BigInteger.ONE, false);
        return LLVM.BuildAdd(functionScope.getBuilder(), left, right, functionScope.uniqueName("incr"));
    }

    @Override
    public ValueRef visit(EDecr exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = LLVM.ConstInt(LLVM.Int32Type(), BigInteger.ONE, false);
        return LLVM.BuildSub(functionScope.getBuilder(), left, right, functionScope.uniqueName("decr"));
    }

    @Override
    public ValueRef visit(ETimes exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);
        return LLVM.BuildMul(functionScope.getBuilder(), left, right, functionScope.uniqueName("times"));
    }

    @Override
    public ValueRef visit(EDiv exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);
        return LLVM.BuildSDiv(functionScope.getBuilder(), left, right, functionScope.uniqueName("div"));
    }

    @Override
    public ValueRef visit(EPlus exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);
        return LLVM.BuildAdd(functionScope.getBuilder(), left, right, functionScope.uniqueName("plus"));
    }

    @Override
    public ValueRef visit(EMinus exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);
        return LLVM.BuildSub(functionScope.getBuilder(), left, right, functionScope.uniqueName("minus"));
    }

    @Override
    public ValueRef visit(ELt exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);
        return LLVM.BuildICmp(functionScope.getBuilder(), IntPredicate.IntSLT, left, right, functionScope.uniqueName("lt"));
    }

    @Override
    public ValueRef visit(EGt exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);
        return LLVM.BuildICmp(functionScope.getBuilder(), IntPredicate.IntSGT, left, right, functionScope.uniqueName("gt"));
    }

    @Override
    public ValueRef visit(ELtEq exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);
        return LLVM.BuildICmp(functionScope.getBuilder(), IntPredicate.IntSLE, left, right, functionScope.uniqueName("lteq"));
    }

    @Override
    public ValueRef visit(EGtEq exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);
        return LLVM.BuildICmp(functionScope.getBuilder(), IntPredicate.IntSGE, left, right, functionScope.uniqueName("gteq"));
    }

    @Override
    public ValueRef visit(EEq exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);
        return LLVM.BuildICmp(functionScope.getBuilder(), IntPredicate.IntEQ, left, right, functionScope.uniqueName("eq"));
    }

    @Override
    public ValueRef visit(ENEq exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);
        return LLVM.BuildICmp(functionScope.getBuilder(), IntPredicate.IntNE, left, right, functionScope.uniqueName("neq"));
    }

    @Override
    public ValueRef visit(EAnd exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);
        return LLVM.BuildAnd(functionScope.getBuilder(), left, right, functionScope.uniqueName("and"));
    }

    @Override
    public ValueRef visit(EOr exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);
        return LLVM.BuildOr(functionScope.getBuilder(), left, right, functionScope.uniqueName("or"));
    }

    @Override
    public ValueRef visit(EAss exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);
        LLVM.BuildStore(functionScope.getBuilder(), right, left);
        return left;
    }

    @Override
    public ValueRef visit(ETyped exp, FunctionScope functionScope) {
        return exp.exp_.accept(new ExpressionInterpreter(), functionScope);
    }
}
