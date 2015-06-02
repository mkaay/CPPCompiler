import CPP.Absyn.Program;
import CPP.Yylex;
import CPP.parser;
import group11.typechecker.TypeChecker;
import group11.typechecker.TypeException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TypeCheckerTest {
    @DataProvider(name = "sources_good")
    public Iterator<Object[]> sourcesGood(Method m) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loadDir(new File(loader.getResource("good").getPath()));
    }

    @DataProvider(name = "sources_bad")
    public Iterator<Object[]> sourcesBad(Method m) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loadDir(new File(loader.getResource("bad").getPath()));
    }

    public Iterator<Object[]> loadDir(File dir) {
        List<Object[]> data = new LinkedList<>();

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                data.add(new Object[]{file});
            }
        }

        return data.iterator();
    }

    @Test(dataProvider = "sources_good")
    public void testGood(File file) throws Exception {
        Assert.assertTrue(typecheck(file));
    }

    @Test(dataProvider = "sources_bad")
    public void testBad(File file) throws Exception {
        Assert.assertFalse(typecheck(file));
    }

    public boolean typecheck(File file) throws Exception {
        try {
            Yylex l = new Yylex(new FileReader(file));
            parser p = new parser(l);
            Program parse_tree = p.pProgram();
            new TypeChecker().typecheck(parse_tree);

            return true;
        } catch (TypeException e) {
            return false;
        }
    }
}