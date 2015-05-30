package CPP;

import CPP.Absyn.*;

import java.util.HashMap;
import java.util.LinkedList;


public class TypeChecker {

    private enum TypeCode {
        BOOL {
            public String toString() {
                return "bool";
            }
        },
        INT {
            public String toString() {
                return "int";
            }
        },
        DOUBLE {
            public String toString() {
                return "double";
            }
        },
        VOID {
            public String toString() {
                return "void";
            }
        },
        STRING {
            public String toString() {
                return "string";
            }
        }
    }

    private static class Env {
        private LinkedList<HashMap<String, TypeCode>> scopes;

        public Env() {
            scopes = new LinkedList<>();
            enterScope();
        }

        public TypeCode lookupVar(String x) {
            for (HashMap<String, TypeCode> scope : scopes) {
                TypeCode t = scope.get(x);
                if (t != null) {
                    return t;
                }
            }
            throw new TypeException(String.format("Unknown variable %s.", x));
        }

        public void addVar(String x, TypeCode t) {
            if (scopes.getFirst().containsKey(x)) {
                throw new TypeException(String.format("Variable %s is already declared in this scope.", x));
            }
            scopes.getFirst().put(x, t);
        }

        public void enterScope() {
            scopes.addFirst(new HashMap<String, TypeCode>());
        }

        public void leaveScope() {
            scopes.removeFirst();
        }
    }

    public void typecheck(Program p) {
        PDefs prog = (PDefs) p;
        Env env = new Env();
        for (Def def : prog.listdef_) {
            checkDef(def, env);
        }
    }

    private void checkDef(Def def, Env env) {
        def.accept(new DefChecker(), env);
    }

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

        @Override
        public Object visit(DFun p, Env arg) {
            return null;
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

    private TypeCode inferExp(Exp e, Env env) {
        return e.accept(new TypeInferrer(), env);
    }

    private class TypeInferrer implements Exp.Visitor<TypeCode, Env> {

        public TypeCode visit(EVar p, Env env) {
            return env.lookupVar(p.ident_);
        }

        public TypeCode visit(EInt p, Env env) {
            return TypeCode.INT;
        }

        public TypeCode visit(EDouble p, Env env) {
            return TypeCode.DOUBLE;
        }

        public TypeCode visit(EAdd p, Env env) {
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

    }

    private TypeCode typeCode(Type t) {
        return t.accept(new TypeCoder(), null);
    }

    private class TypeCoder implements Type.Visitor<TypeCode, Object> {
        public TypeCode visit(TInt t, Object arg) {
            return TypeCode.INT;
        }

        public TypeCode visit(TDouble t, Object arg) {
            return TypeCode.DOUBLE;
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
