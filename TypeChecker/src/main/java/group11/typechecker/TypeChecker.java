package group11.typechecker;

import CPP.Absyn.*;
import CPP.Yylex;
import CPP.parser;

import java.util.LinkedList;
import java.util.List;

public class TypeChecker {
    public void typecheck(Program p) {
        PDefs prog = (PDefs) p;
        Environment env = new Environment();

        // build signature table
        for (Def def : prog.listdef_) {
            def.accept(new Def.Visitor<Object, Environment>() {
                @Override
                public Object visit(DFun f, Environment env) {
                    List<TypeCode> args = new LinkedList<>();
                    for (Arg arg : f.listarg_) {
                        ADecl a = arg.accept(new ArgumentChecker(), env);
                        args.add(TypeCoder.fromType(a.type_));
                    }
                    env.addFunction(f.id_, new FunctionType(args, TypeCoder.fromType(f.type_)));

                    return null;
                }
            }, env);
        }

        // check types
        for (Def def : prog.listdef_) {
            def.accept(new FunctionChecker(), env);
        }
    }

    public static void main(String args[]) {
        if (args.length != 1) {
            System.err.println("No input file!");
            System.exit(1);
        }

        Yylex l = null;
        try {
            l = new Yylex(new java.io.FileReader(args[0]));
            parser p = new parser(l);
            Program parse_tree = p.pProgram();
            new TypeChecker().typecheck(parse_tree);

            System.out.println("OK");
        } catch (TypeException e) {
            System.out.println("TYPE ERROR");
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (RuntimeException e) {
            System.out.println("RUNTIME ERROR");
            System.err.println(e.toString());
            e.printStackTrace();
            System.exit(1);
        } catch (java.io.IOException e) {
            System.err.println(e.toString());
            System.exit(1);
        } catch (Throwable e) {
            System.out.println("SYNTAX ERROR");
            System.err.println(String.format("At line %d, near '%s':\n%s", l.line_num(), l.buff(), e.getMessage()));
            e.printStackTrace();
            System.exit(1);
        }
    }

}
