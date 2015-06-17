package group11.interpreter.visitors;

import CPP.Absyn.*;
import group11.interpreter.helpers.FunctionScope;
import group11.interpreter.helpers.SmartValueRefArray;
import org.robovm.llvm.binding.IntPredicate;
import org.robovm.llvm.binding.LLVM;
import org.robovm.llvm.binding.RealPredicate;
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
            args.add(ensureValue(arg.accept(new ExpressionInterpreter(), functionScope), functionScope));
        }

        // void return functions can't have named return
        String returnName = "";
        if (!LLVM.GetReturnType(LLVM.GetReturnType(LLVM.TypeOf(FunctionScope.getFunction(exp.id_)))).equals(LLVM.VoidType())) {
            returnName = functionScope.uniqueName(String.format("%s_ret", exp.id_));
        }

        return LLVM.BuildCall(functionScope.getBuilder(), FunctionScope.getFunction(exp.id_),
                args, exp.listexp_.size(), returnName);
    }

    @Override
    public ValueRef visit(EPIncr exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_.accept(new ExpressionInterpreter(), functionScope);

        if (LLVM.TypeOf(left).equals(LLVM.Int32Type())) {
            ValueRef right = LLVM.ConstInt(LLVM.Int32Type(), BigInteger.ONE, false);
            ValueRef ret = LLVM.BuildAdd(functionScope.getBuilder(), left, right, functionScope.uniqueName("pincr"));
            LLVM.BuildStore(functionScope.getBuilder(), ret, left);
            return ret;
        } else {
            ValueRef right = LLVM.ConstReal(LLVM.DoubleType(), 1);
            ValueRef ret = LLVM.BuildFAdd(functionScope.getBuilder(), left, right, functionScope.uniqueName("pincr"));
            LLVM.BuildStore(functionScope.getBuilder(), ret, left);
            return ret;
        }
    }

    @Override
    public ValueRef visit(EPDecr exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_.accept(new ExpressionInterpreter(), functionScope);

        if (LLVM.TypeOf(left).equals(LLVM.Int32Type())) {
            ValueRef right = LLVM.ConstInt(LLVM.Int32Type(), BigInteger.ONE, false);
            ValueRef ret = LLVM.BuildSub(functionScope.getBuilder(), left, right, functionScope.uniqueName("pdecr"));
            LLVM.BuildStore(functionScope.getBuilder(), ret, left);
            return ret;
        } else {
            ValueRef right = LLVM.ConstReal(LLVM.DoubleType(), 1);
            ValueRef ret = LLVM.BuildFSub(functionScope.getBuilder(), left, right, functionScope.uniqueName("pdecr"));
            LLVM.BuildStore(functionScope.getBuilder(), ret, left);
            return ret;
        }
    }

    @Override
    public ValueRef visit(EIncr exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_.accept(new ExpressionInterpreter(), functionScope);
        if (LLVM.TypeOf(left).equals(LLVM.Int32Type())) {
            ValueRef right = LLVM.ConstInt(LLVM.Int32Type(), BigInteger.ONE, false);
            return LLVM.BuildAdd(functionScope.getBuilder(), left, right, functionScope.uniqueName("incr"));
        } else {
            ValueRef right = LLVM.ConstReal(LLVM.DoubleType(), 1);
            return LLVM.BuildFAdd(functionScope.getBuilder(), left, right, functionScope.uniqueName("incr"));
        }
    }

    @Override
    public ValueRef visit(EDecr exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_.accept(new ExpressionInterpreter(), functionScope);
        if (LLVM.TypeOf(left).equals(LLVM.Int32Type())) {
            ValueRef right = LLVM.ConstInt(LLVM.Int32Type(), BigInteger.ONE, false);
            return LLVM.BuildSub(functionScope.getBuilder(), left, right, functionScope.uniqueName("decr"));
        } else {
            ValueRef right = LLVM.ConstReal(LLVM.DoubleType(), 1);
            return LLVM.BuildFSub(functionScope.getBuilder(), left, right, functionScope.uniqueName("decr"));
        }
    }

    @Override
    public ValueRef visit(ETimes exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);

        left = ensureValue(left, functionScope);
        right = ensureValue(right, functionScope);

        if (LLVM.TypeOf(left).equals(LLVM.Int32Type())) {
            return LLVM.BuildMul(functionScope.getBuilder(), left, right, functionScope.uniqueName("times"));
        } else {
            return LLVM.BuildFMul(functionScope.getBuilder(), left, right, functionScope.uniqueName("times"));
        }
    }

    @Override
    public ValueRef visit(EDiv exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);

        left = ensureValue(left, functionScope);
        right = ensureValue(right, functionScope);

        if (LLVM.TypeOf(left).equals(LLVM.Int32Type())) {
            return LLVM.BuildSDiv(functionScope.getBuilder(), left, right, functionScope.uniqueName("div"));
        } else {
            return LLVM.BuildFDiv(functionScope.getBuilder(), left, right, functionScope.uniqueName("div"));
        }
    }

    @Override
    public ValueRef visit(EPlus exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);

        left = ensureValue(left, functionScope);
        right = ensureValue(right, functionScope);

        if (LLVM.TypeOf(left).equals(LLVM.Int32Type())) {
            return LLVM.BuildAdd(functionScope.getBuilder(), left, right, functionScope.uniqueName("plus"));
        } else {
            return LLVM.BuildFAdd(functionScope.getBuilder(), left, right, functionScope.uniqueName("plus"));
        }
    }

    @Override
    public ValueRef visit(EMinus exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);

        left = ensureValue(left, functionScope);
        right = ensureValue(right, functionScope);

        if (LLVM.TypeOf(left).equals(LLVM.Int32Type())) {
            return LLVM.BuildSub(functionScope.getBuilder(), left, right, functionScope.uniqueName("minus"));
        } else {
            return LLVM.BuildFSub(functionScope.getBuilder(), left, right, functionScope.uniqueName("minus"));
        }
    }

    @Override
    public ValueRef visit(ELt exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);

        left = ensureValue(left, functionScope);
        right = ensureValue(right, functionScope);

        if (LLVM.TypeOf(left).equals(LLVM.Int32Type())) {
            return LLVM.BuildICmp(functionScope.getBuilder(), IntPredicate.IntSLT, left, right, functionScope.uniqueName("lt"));
        } else {
            return LLVM.BuildFCmp(functionScope.getBuilder(), RealPredicate.RealULT, left, right, functionScope.uniqueName("lt"));
        }
    }

    @Override
    public ValueRef visit(EGt exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);

        left = ensureValue(left, functionScope);
        right = ensureValue(right, functionScope);

        if (LLVM.TypeOf(left).equals(LLVM.Int32Type())) {
            return LLVM.BuildICmp(functionScope.getBuilder(), IntPredicate.IntSGT, left, right, functionScope.uniqueName("gt"));
        } else {
            return LLVM.BuildFCmp(functionScope.getBuilder(), RealPredicate.RealUGT, left, right, functionScope.uniqueName("gt"));
        }
    }

    @Override
    public ValueRef visit(ELtEq exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);

        left = ensureValue(left, functionScope);
        right = ensureValue(right, functionScope);

        if (LLVM.TypeOf(left).equals(LLVM.Int32Type())) {
            return LLVM.BuildICmp(functionScope.getBuilder(), IntPredicate.IntSLE, left, right, functionScope.uniqueName("lteq"));
        } else {
            return LLVM.BuildFCmp(functionScope.getBuilder(), RealPredicate.RealULE, left, right, functionScope.uniqueName("lteq"));
        }
    }

    @Override
    public ValueRef visit(EGtEq exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);

        left = ensureValue(left, functionScope);
        right = ensureValue(right, functionScope);

        if (LLVM.TypeOf(left).equals(LLVM.Int32Type())) {
            return LLVM.BuildICmp(functionScope.getBuilder(), IntPredicate.IntSGE, left, right, functionScope.uniqueName("gteq"));
        } else {
            return LLVM.BuildFCmp(functionScope.getBuilder(), RealPredicate.RealUGE, left, right, functionScope.uniqueName("gteq"));
        }
    }

    @Override
    public ValueRef visit(EEq exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);

        left = ensureValue(left, functionScope);
        right = ensureValue(right, functionScope);

        if (LLVM.TypeOf(left).equals(LLVM.Int32Type())) {
            return LLVM.BuildICmp(functionScope.getBuilder(), IntPredicate.IntEQ, left, right, functionScope.uniqueName("eq"));
        } else {
            return LLVM.BuildFCmp(functionScope.getBuilder(), RealPredicate.RealUEQ, left, right, functionScope.uniqueName("eq"));
        }
    }

    @Override
    public ValueRef visit(ENEq exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);

        left = ensureValue(left, functionScope);
        right = ensureValue(right, functionScope);

        if (LLVM.TypeOf(left).equals(LLVM.Int32Type())) {
            return LLVM.BuildICmp(functionScope.getBuilder(), IntPredicate.IntNE, left, right, functionScope.uniqueName("neq"));
        } else {
            return LLVM.BuildFCmp(functionScope.getBuilder(), RealPredicate.RealUNE, left, right, functionScope.uniqueName("neq"));
        }
    }

    @Override
    public ValueRef visit(EAnd exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);

        left = ensureValue(left, functionScope);
        right = ensureValue(right, functionScope);

        return LLVM.BuildAnd(functionScope.getBuilder(), left, right, functionScope.uniqueName("and"));
    }

    @Override
    public ValueRef visit(EOr exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);

        left = ensureValue(left, functionScope);
        right = ensureValue(right, functionScope);

        return LLVM.BuildOr(functionScope.getBuilder(), left, right, functionScope.uniqueName("or"));
    }

    @Override
    public ValueRef visit(EAss exp, FunctionScope functionScope) {
        ValueRef left = exp.exp_1.accept(new ExpressionInterpreter(), functionScope);
        ValueRef right = exp.exp_2.accept(new ExpressionInterpreter(), functionScope);

        //left = ensureValue(left, functionScope);
        right = ensureValue(right, functionScope);

        //ValueRef value = LLVM.BuildLoad(functionScope.getBuilder(), right, functionScope.uniqueName("tmp"));

        LLVM.BuildStore(functionScope.getBuilder(), right, left);
        return left;
    }

    @Override
    public ValueRef visit(ETyped exp, FunctionScope functionScope) {
        return exp.exp_.accept(new ExpressionInterpreter(), functionScope);
    }

    public static ValueRef ensureValue(ValueRef value, FunctionScope functionScope) {
        //System.out.println(String.format("before: %s", LLVM.PrintTypeToString(LLVM.TypeOf(value))));

        if (LLVM.TypeOf(value).equals(LLVM.PointerType(LLVM.Int32Type(), 0))) {
            value = LLVM.BuildLoad(functionScope.getBuilder(), value, String.format("%s_value", LLVM.GetValueName(value)));

        } else if (LLVM.TypeOf(value).equals(LLVM.PointerType(LLVM.DoubleType(), 0))) {
            value = LLVM.BuildLoad(functionScope.getBuilder(), value, String.format("%s_value", LLVM.GetValueName(value)));

        }

        //System.out.println(String.format("after: %s", LLVM.PrintTypeToString(LLVM.TypeOf(value))));

        return value;
    }
}
