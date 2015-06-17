package group11.interpreter.visitors;

import CPP.Absyn.*;
import group11.interpreter.helpers.FunctionScope;
import org.robovm.llvm.binding.BasicBlockRef;
import org.robovm.llvm.binding.LLVM;
import org.robovm.llvm.binding.TypeRef;
import org.robovm.llvm.binding.ValueRef;

public class StatementInterpreter implements Stm.Visitor<Object, FunctionScope> {
    @Override
    public Object visit(SExp statement, FunctionScope functionScope) {
        statement.exp_.accept(new ExpressionInterpreter(), functionScope);

        return null;
    }

    @Override
    public Object visit(SDecls statement, FunctionScope functionScope) {
        TypeRef type = statement.type_.accept(new TypeInterpreter(), null);

        for (String name : statement.listid_) {
            functionScope.addVariable(name, type);
        }

        return null;
    }

    @Override
    public Object visit(SInit statement, FunctionScope functionScope) {
        TypeRef type = statement.type_.accept(new TypeInterpreter(), null);

        ValueRef variable = functionScope.addVariable(statement.id_, type);

        ValueRef value = statement.exp_.accept(new ExpressionInterpreter(), functionScope);

        LLVM.BuildStore(functionScope.getBuilder(), value, variable);

        return null;
    }

    @Override
    public Object visit(SReturn statement, FunctionScope functionScope) {
        ValueRef value = statement.exp_.accept(new ExpressionInterpreter(), functionScope);

        LLVM.BuildRet(functionScope.getBuilder(), ExpressionInterpreter.ensureValue(value, functionScope));

        return null;
    }

    @Override
    public Object visit(SReturnVoid statement, FunctionScope functionScope) {
        LLVM.BuildRetVoid(functionScope.getBuilder());

        return null;
    }

    /**
     * <last_block>:
     * br label %while.cond
     * <p/>
     * while.cond:
     * %cmp = <cond>
     * br i1 %cmp, label %while.body, label %while.end
     * <p/>
     * while.body:
     * <stm>
     * br label %while.cond
     * <p/>
     * while.end:
     * <new_block>
     */
    @Override
    public Object visit(SWhile statement, FunctionScope functionScope) {
        BasicBlockRef blockCond = functionScope.createBlock(functionScope.uniqueName("while.cond"));
        BasicBlockRef blockBody = functionScope.createBlock(functionScope.uniqueName("while.body"));
        BasicBlockRef blockEnd = functionScope.createBlock(functionScope.uniqueName("while.end"));

        LLVM.BuildBr(functionScope.getBuilder(), blockCond);

        functionScope.enterBlock(blockCond);
        ValueRef cond = statement.exp_.accept(new ExpressionInterpreter(), functionScope);
        LLVM.BuildCondBr(functionScope.getBuilder(), cond, blockBody, blockEnd);
        functionScope.leaveBlock();

        functionScope.enterBlock(blockBody);
        statement.stm_.accept(this, functionScope);
        LLVM.BuildBr(functionScope.getBuilder(), blockEnd);
        functionScope.leaveBlock();

        functionScope.enterBlock(blockEnd);

        return null;
    }

    @Override
    public Object visit(SBlock statement, FunctionScope functionScope) {
        functionScope.enterScope();

        for (Stm s : statement.liststm_) {
            s.accept(this, functionScope);
        }

        functionScope.leaveScope();

        return null;
    }

    /**
     * <last_block>:
     * br label %if.cond
     * <p/>
     * if.cond:
     * %cmp = <cond>
     * br i1 %cmp, label %if.then, label %if.else
     * <p/>
     * if.then:
     * <stm_1>
     * br label %if.end
     * <p/>
     * if.else:
     * <stm_2>
     * br label %if.end
     * <p/>
     * if.end:
     * <new_block>
     */
    @Override
    public Object visit(SIfElse statement, FunctionScope functionScope) {
        BasicBlockRef blockCond = functionScope.createBlock(functionScope.uniqueName("if.cond"));
        BasicBlockRef blockThen = functionScope.createBlock(functionScope.uniqueName("if.then"));
        BasicBlockRef blockElse = functionScope.createBlock(functionScope.uniqueName("if.else"));
        BasicBlockRef blockEnd = functionScope.createBlock(functionScope.uniqueName("if.end"));

        LLVM.BuildBr(functionScope.getBuilder(), blockCond);

        functionScope.enterBlock(blockCond);
        ValueRef cond = statement.exp_.accept(new ExpressionInterpreter(), functionScope);
        LLVM.BuildCondBr(functionScope.getBuilder(), cond, blockThen, blockElse);
        functionScope.leaveBlock();

        functionScope.enterBlock(blockThen);
        statement.stm_1.accept(this, functionScope);
        LLVM.BuildBr(functionScope.getBuilder(), blockEnd);
        functionScope.leaveBlock();

        functionScope.enterBlock(blockElse);
        statement.stm_2.accept(this, functionScope);
        LLVM.BuildBr(functionScope.getBuilder(), blockEnd);
        functionScope.leaveBlock();

        functionScope.enterBlock(blockEnd);

        return null;
    }
}
