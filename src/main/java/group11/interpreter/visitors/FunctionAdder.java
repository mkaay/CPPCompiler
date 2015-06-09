package group11.interpreter.visitors;

import CPP.Absyn.Arg;
import CPP.Absyn.DFun;
import CPP.Absyn.Def;
import group11.interpreter.helpers.SmartTypeRefArray;
import org.robovm.llvm.binding.LLVM;
import org.robovm.llvm.binding.ModuleRef;
import org.robovm.llvm.binding.TypeRef;
import org.robovm.llvm.binding.ValueRef;

public class FunctionAdder implements Def.Visitor<ValueRef, ModuleRef> {
    @Override
    public ValueRef visit(DFun function, ModuleRef module) {
        SmartTypeRefArray types = new SmartTypeRefArray(function.listarg_.size());

        for (Arg arg : function.listarg_) {
            arg.accept(new ArgumentInterpreter(), types);
        }

        TypeRef returnType = function.type_.accept(new TypeInterpreter(), null);
        TypeRef functionType = LLVM.FunctionType(returnType, types, function.listarg_.size(), false);

        return LLVM.AddFunction(module, function.id_, functionType);
    }
}
