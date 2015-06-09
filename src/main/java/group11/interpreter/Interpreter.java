package group11.interpreter;

import CPP.Absyn.Def;
import CPP.Absyn.PDefs;
import CPP.Absyn.Program;
import group11.interpreter.visitors.FunctionAdder;
import group11.interpreter.visitors.FunctionInterpreter;
import org.robovm.llvm.binding.ContextRef;
import org.robovm.llvm.binding.LLVM;
import org.robovm.llvm.binding.ModuleRef;

public class Interpreter {
    private final ContextRef context = LLVM.ContextCreate();
    private final ModuleRef module;

    private Interpreter(String name) {
        module = LLVM.ModuleCreateWithNameInContext(name, context);
    }

    private void interpretProgram(Program program) {
        PDefs defs = (PDefs) program;

        for (Def def : defs.listdef_) {
            def.accept(new FunctionAdder(), module);
        }

        for (Def def : defs.listdef_) {
            def.accept(new FunctionInterpreter(), module);
        }
    }

    public static String eval(Program program) {
        return eval(program, "unnamed");
    }

    public static String eval(Program program, String name) {
        Interpreter interpreter = new Interpreter(name);
        interpreter.interpretProgram(program);

        return "";
    }

    public static void main(String args[]) {

    }
}
