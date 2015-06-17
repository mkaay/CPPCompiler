package group11.interpreter;

import CPP.Absyn.Def;
import CPP.Absyn.PDefs;
import CPP.Absyn.Program;
import group11.interpreter.visitors.FunctionAdder;
import group11.interpreter.visitors.FunctionInterpreter;
import org.robovm.llvm.binding.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

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

        //dump();


        PassManagerRef passManager = LLVM.CreatePassManager();
        PassManagerBuilderRef passBuilder = LLVM.PassManagerBuilderCreate();

        LLVM.PassManagerBuilderSetOptLevel(passBuilder, 2);
        LLVM.PassManagerBuilderSetDisableTailCalls(passBuilder, true);
        LLVM.PassManagerBuilderUseAlwaysInliner(passBuilder, true);

        LLVM.PassManagerBuilderPopulateModulePassManager(passBuilder, passManager);


        //LLVM.RunPassManager(passManager, module);
        // broken?


        //LLVM.DumpModule(module);
    }

    public static String eval(Program program) {
        return eval(program, "unnamed");
    }

    public static void eval(Program program, File file) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(file);
        out.println(eval(program, file.getName()));
        out.flush();
        out.close();
    }

    public static String eval(Program program, String name) {
        Interpreter interpreter = new Interpreter(name);
        interpreter.interpretProgram(program);
        //interpreter.dump();

        return interpreter.getModule();
    }

    private String getModule() {
        return LLVM.PrintModuleToString(module);
    }

    private void dump() {
        LLVM.DumpModule(module);
    }

    public static void main(String args[]) {

    }
}
