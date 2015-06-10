package group11.interpreter.visitors;

import CPP.Absyn.*;
import group11.interpreter.helpers.FunctionScope;
import org.robovm.llvm.binding.*;


public class FunctionInterpreter implements Def.Visitor<ValueRef, ModuleRef> {
    @Override
    public ValueRef visit(DFun function, ModuleRef module) {
        final ValueRef functionRef = FunctionScope.getFunction(function.id_);

        final FunctionScope functionScope = new FunctionScope(functionRef, module);

        int i = 0;
        for (Arg arg : function.listarg_) {
            arg.accept(new Arg.Visitor<Object, Integer>() {
                @Override
                public Object visit(ADecl arg, Integer argNum) {
                    TypeRef type = arg.type_.accept(new TypeInterpreter(), null);
                    functionScope.addVariable(arg.id_, type, LLVM.GetParam(functionRef, argNum));

                    return null;
                }
            }, i++);
        }

        for (Stm statement : function.liststm_) {
            statement.accept(new StatementInterpreter(), functionScope);
        }


        PassManagerRef passManager = LLVM.CreateFunctionPassManagerForModule(module);
        LLVM.AddBasicAliasAnalysisPass(passManager);
        LLVM.AddInstructionCombiningPass(passManager);
        LLVM.AddReassociatePass(passManager);
        LLVM.AddGVNPass(passManager);
        LLVM.AddCFGSimplificationPass(passManager);

        LLVM.InitializeFunctionPassManager(passManager);
        //LLVM.RunFunctionPassManager(passManager, functionRef);
        // broken?


        return functionRef;
    }
}
