package group11.interpreter.visitors;

import CPP.Absyn.ADecl;
import CPP.Absyn.Arg;
import group11.interpreter.helpers.SmartTypeRefArray;
import org.robovm.llvm.binding.TypeRef;

public class ArgumentInterpreter implements Arg.Visitor<TypeRef, SmartTypeRefArray> {
    @Override
    public TypeRef visit(ADecl argument, SmartTypeRefArray arguments) {
        TypeRef type = argument.type_.accept(new TypeInterpreter(), null);
        arguments.add(type);
        return type;
    }
}
