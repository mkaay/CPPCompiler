import CPP.Absyn.Program;
import CPP.TypeChecker;
import CPP.TypeException;
import CPP.Yylex;
import CPP.parser;
import org.junit.*;

import java.io.File;

import static org.junit.Assert.*;

public class TypeCheckerTest {

    @org.junit.Test
    public void testTypecheckGood() throws Exception {
        File dir = new File("resources/good/");

        File[] filesList = dir.listFiles();
        for (File file : filesList) {
            if (file.isFile()) {
                System.out.println(file.getName());

                Assert.assertTrue(typecheck(file));
            }
        }
    }

    @org.junit.Test
    public void testTypecheckBad() throws Exception {
        File dir = new File("resources/bad/");

        File[] filesList = dir.listFiles();
        for (File file : filesList) {
            if (file.isFile()) {
                System.out.println(file.getName());

                Assert.assertFalse(typecheck(file));
            }
        }
    }

    public boolean typecheck(File file) {
        Yylex l = null;
        try {
            l = new Yylex(new java.io.FileReader(file));
            parser p = new parser(l);
            Program parse_tree = p.pProgram();
            new TypeChecker().typecheck(parse_tree);

            System.out.println("OK");
            return true;
        } catch (TypeException e) {
            System.out.println("TYPE ERROR");
            System.err.println(e.toString());
            System.out.println(e.getEnv());
        } catch (RuntimeException e) {
            System.out.println("RUNTIME ERROR");
            System.err.println(e.toString());
        } catch (java.io.IOException e) {
            System.err.println(e.toString());
        } catch (Throwable e) {
            System.out.println("SYNTAX ERROR");
            System.out.println("At line " + String.valueOf(l.line_num())
                    + ", near \"" + l.buff() + "\" :");
            System.out.println("     " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}