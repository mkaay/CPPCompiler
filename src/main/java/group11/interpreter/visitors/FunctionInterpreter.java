package group11.interpreter.visitors;

import CPP.Absyn.DFun;
import CPP.Absyn.Def;
import CPP.Absyn.Stm;
import group11.interpreter.helpers.FunctionScope;
import org.robovm.llvm.binding.LLVM;
import org.robovm.llvm.binding.ModuleRef;
import org.robovm.llvm.binding.ValueRef;


public class FunctionInterpreter implements Def.Visitor<Object, ModuleRef> {
    @Override
    public Object visit(DFun function, ModuleRef module) {
        ValueRef functionRef = LLVM.GetNamedFunction(module, function.id_);

        FunctionScope functionScope = new FunctionScope(functionRef);

        for (Stm statement : function.liststm_) {
            statement.accept(new StatementInterpreter(), functionScope);
        }

        return null;
    }
}
