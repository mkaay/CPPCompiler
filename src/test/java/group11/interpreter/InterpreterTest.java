package group11.interpreter;

import CPP.Absyn.Program;
import CPP.Yylex;
import CPP.parser;
import org.robovm.llvm.LlvmException;
import org.robovm.llvm.binding.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class InterpreterTest {

    //@Test
    public void test() {
        // api playground

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

    @DataProvider(name = "sources")
    public Iterator<Object[]> sources(Method m) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loadDir(new File(loader.getResource("good").getPath()));
    }

    public Iterator<Object[]> loadDir(File dir) {
        List<Object[]> data = new LinkedList<>();

        for (File file : dir.listFiles()) {
            if (file.isFile() && file.getName().endsWith("cc")) {
                data.add(new Object[]{file});
            }
        }

        return data.iterator();
    }

    @Test(dataProvider = "sources")
    public void testGood(File file) throws Exception {
        Assert.assertTrue(interpret(file));
    }

    @Test
    public boolean interpret(File file) throws Exception {
        new File("llvm_out").mkdirs();

        try {
            Yylex l = new Yylex(new FileReader(file));
            parser p = new parser(l);
            Program parse_tree = p.pProgram();
            Interpreter.eval(parse_tree, new File(String.format("llvm_out/%s.ll", file.getName().split("\\.")[0])));

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

        // bindings will segfault if we do something wrong, so we can return true
        return true;
    }
}