package group11.interpreter;

import CPP.Absyn.Program;
import CPP.Yylex;
import CPP.parser;
import org.robovm.llvm.LlvmException;
import org.robovm.llvm.binding.*;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;

public class InterpreterTest {

    //@Test
    public void test() {
        ModuleRef mod = LLVM.ModuleCreateWithName("test");

        TypeRefArray param_types = new TypeRefArray(2);
        param_types.set(0, LLVM.Int32Type());
        param_types.set(1, LLVM.Int32Type());
        TypeRef ret_type = LLVM.FunctionType(LLVM.Int32Type(), param_types, 2, false);
        ValueRef sum = LLVM.AddFunction(mod, "sum", ret_type);

        BasicBlockRef entry = LLVM.AppendBasicBlock(sum, "entry");
        BuilderRef builder = LLVM.CreateBuilder();
        LLVM.PositionBuilderAtEnd(builder, entry);

        ValueRef tmp = LLVM.BuildAdd(builder, LLVM.GetParam(sum, 0), LLVM.GetParam(sum, 1), "tmp");
        LLVM.BuildRet(builder, tmp);


        PassManagerRef passManager = LLVM.CreatePassManager();
        PassManagerBuilderRef passBuilder = LLVM.PassManagerBuilderCreate();

        LLVM.PassManagerBuilderSetOptLevel(passBuilder, 2);
        LLVM.PassManagerBuilderSetDisableTailCalls(passBuilder, true);
        LLVM.PassManagerBuilderUseAlwaysInliner(passBuilder, true);

        LLVM.PassManagerBuilderPopulateModulePassManager(passBuilder, passManager);
        LLVM.RunPassManager(passManager, mod);

        StringOut ErrorMessage = new StringOut();
        TargetRef target = LLVM.LookupTarget(LLVM.getLlvmHostTriple(), ErrorMessage);
        if (target == null) {
            throw new LlvmException(ErrorMessage.getValue().trim());
        }

        TargetMachineRef machine = LLVM.CreateTargetMachine(target, LLVM.getLlvmHostTriple(), "", "", CodeGenOptLevel.CodeGenLevelDefault, RelocMode.RelocDefault, CodeModel.CodeModelDefault);

        if (LLVM.TargetMachineEmitToFile(machine, mod, "test.asm", CodeGenFileType.AssemblyFile, ErrorMessage)) {
            // Returns true on failure!
            throw new LlvmException(ErrorMessage.getValue().trim());
        }

        //LLVM.PrintModuleToFile(mod, "test.ll", ErrorMessage);
        //LLVM.DumpModule(mod);
    }

    @Test
    public void interpret() throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            Yylex l = new Yylex(new FileReader(new File(loader.getResource("interpreter/core001.cc").getPath())));
            parser p = new parser(l);
            Program parse_tree = p.pProgram();
            Interpreter.eval(parse_tree, new File("core001.ll"));

            //System.out.print(file.getName());
            //System.out.println(": OK");

            //    return true;
            //} catch (InterpreterException e) {
            //System.out.print(file.getName());
            //System.out.print(": ");
            //System.out.println(e.getMessage());
            //    return false;
            //}
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}