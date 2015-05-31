package CPP;

import CPP.Absyn.*;

import java.util.LinkedList;
import java.util.List;


public class TypeChecker {


    public void typecheck(Program p) {
        PDefs prog = (PDefs) p;
        Env env = new Env();
        for (Def def : prog.listdef_) {
            def.accept(new CheckDef(), env);
        }
    }

    private class CheckDef implements Def.Visitor<Object, Env> {
        @Override
        public Object visit(DFun f, Env env) {
            checkFun(f, env);
            return null;
        }
    }

    /*
    private class DefChecker implements Def.Visitor<Object, Env> {
        public Object visit(SDecl p, Env env) {
            env.addVar(p.ident_, typeCode(p.type_));
            return null;
        }

        public Object visit(SAss p, Env env) {
            TypeCode t = env.lookupVar(p.ident_);
            checkExp(p.exp_, t, env);
            return null;
        }

        public Object visit(SBlock p, Env env) {
            env.enterScope();
            for (Stm st : p.liststm_) {
                checkStm(st, env);
            }
            env.leaveScope();
            return null;
        }

        public Object visit(SPrint p, Env env) {
            // we don't care what the type is, just that there is one
            inferExp(p.exp_, env);
            return null;
        }

    }
    */

    private void checkFun(DFun f, Env env) {
        List<TypeCode> args = new LinkedList<>();
        for (Arg arg : f.listarg_) {
            args.add(arg.accept(new CheckArg(), env));
        }
        env.addFun(f.id_, new FunType(args, typeCode(f.type_)));

        env.enterFunction(f.id_);
        for (Stm st : f.liststm_) {
            st.accept(new CheckStm(), env);
        }
        env.leaveFunction();
    }

    private class CheckStm implements Stm.Visitor<Object,Env> {
        @Override
        public Object visit(SExp stm, Env env) {
            return null;
        }

        @Override
        public Object visit(SDecls stm, Env env) {
            for (String id : stm.listid_) {
                env.addVar(id, typeCode(stm.type_));
            }
            return null;
        }

        @Override
        public Object visit(SInit stm, Env env) {
            TypeCode t = typeCode(stm.type_);
            env.addVar(stm.id_, t);
            checkExp(stm.exp_, t, env);
            return null;
        }

        @Override
        public Object visit(SReturn stm, Env env) {
            TypeCode et = inferExp(stm.exp_, env);
            TypeCode t = env.getReturnType();
            if (et != t) {
                throw new TypeException(PrettyPrinter.print(stm)
                        + " has type " + et
                        + " expected " + t);
            }
            return null;
        }

        @Override
        public Object visit(SReturnVoid stm, Env env) {
            TypeCode et = TypeCode.VOID;
            TypeCode t = env.getReturnType();
            if (et != t) {
                throw new TypeException(PrettyPrinter.print(stm)
                        + " has type " + et
                        + " expected " + t);
            }
            return null;
        }

        @Override
        public Object visit(SWhile stm, Env env) {
            return null;
        }

        @Override
        public Object visit(SBlock stm, Env env) {
            return null;
        }

        @Override
        public Object visit(SIfElse stm, Env env) {
            return null;
        }
    }

    private class CheckArg implements Arg.Visitor<TypeCode, Env> {
        @Override
        public TypeCode visit(ADecl a, Env env) {
            return typeCode(a.type_);
        }
    }

    private void checkExp(Exp e, TypeCode t, Env env) {
        TypeCode et = inferExp(e, env);
        if (et != t) {
            throw new TypeException(PrettyPrinter.print(e)
                    + " has type " + et
                    + " expected " + t);
        }
    }

    private void checkReturn(Exp e, TypeCode t, Env env) {
        TypeCode et = inferExp(e, env);
        if (et != t) {
            throw new TypeException(PrettyPrinter.print(e)
                    + " has type " + et
                    + " expected " + t);
        }
    }

    private TypeCode inferExp(Exp e, Env env) {
        return e.accept(new TypeInferrer(), env);
    }

    private class TypeInferrer implements Exp.Visitor<TypeCode, Env> {

        @Override
        public TypeCode visit(ETrue p, Env env) {
            return TypeCode.BOOL;
        }

        @Override
        public TypeCode visit(EFalse p, Env env) {
            return TypeCode.BOOL;
        }

        @Override
        public TypeCode visit(EInt p, Env env) {
            return TypeCode.INT;
        }

        @Override
        public TypeCode visit(EDouble p, Env env) {
            return TypeCode.DOUBLE;
        }

        @Override
        public TypeCode visit(EString p, Env env) {
            return TypeCode.STRING;
        }

        @Override
        public TypeCode visit(EId p, Env env) {
            return env.lookupVar(p.id_);
        }

        @Override
        public TypeCode visit(EApp p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(EPIncr p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(EPDecr p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(EIncr p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(EDecr p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(ETimes p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(EDiv p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(EPlus p, Env env) {
            TypeCode t1 = p.exp_1.accept(this, env);
            TypeCode t2 = p.exp_2.accept(this, env);

            if (t1 != t2) {
                throw new TypeException(PrettyPrinter.print(p.exp_1) +
                        " has type " + t1
                        + " but " + PrettyPrinter.print(p.exp_1)
                        + " has type " + t2);
            }

            return t1;
        }

        @Override
        public TypeCode visit(EMinus p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(ELt p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(EGt p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(ELtEq p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(EGtEq p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(EEq p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(ENEq p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(EAnd p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(EOr p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(EAss p, Env env) {
            return null;
        }

        @Override
        public TypeCode visit(ETyped p, Env env) {
            return null;
        }

    }

    private TypeCode typeCode(Type t) {
        return t.accept(new TypeCoder(), null);
    }

    private class TypeCoder implements Type.Visitor<TypeCode, Object> {

        @Override
        public TypeCode visit(Type_bool p, Object arg) {
            return TypeCode.BOOL;
        }

        @Override
        public TypeCode visit(Type_int p, Object arg) {
            return TypeCode.INT;
        }

        @Override
        public TypeCode visit(Type_double p, Object arg) {
            return TypeCode.DOUBLE;
        }

        @Override
        public TypeCode visit(Type_void p, Object arg) {
            return TypeCode.VOID;
        }

        @Override
        public TypeCode visit(Type_string p, Object arg) {
            return TypeCode.STRING;
        }
    }

    public static void main(String args[]) {
        Yylex l = null;
        try {
            l = new Yylex(new java.io.FileReader(args[0]));
            parser p = new parser(l);
            Program parse_tree = p.pProgram();
            new TypeChecker().typecheck(parse_tree);
        } catch (TypeException e) {
            System.out.println("TYPE ERROR");
            System.err.println(e.toString());
            System.exit(1);
        } catch (RuntimeException e) {
            System.out.println("RUNTIME ERROR");
            System.err.println(e.toString());
            System.exit(1);
        } catch (java.io.IOException e) {
            System.err.println(e.toString());
            System.exit(1);
        } catch (Throwable e) {
            System.out.println("SYNTAX ERROR");
            System.out.println("At line " + String.valueOf(l.line_num())
                    + ", near \"" + l.buff() + "\" :");
            System.out.println("     " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
