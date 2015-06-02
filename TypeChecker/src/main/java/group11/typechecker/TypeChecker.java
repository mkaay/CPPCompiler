package group11.typechecker;

import CPP.Absyn.*;
import CPP.Yylex;
import CPP.parser;

import java.util.LinkedList;
import java.util.List;


public class TypeChecker {
    public void typecheck(Program p) {
        PDefs prog = (PDefs) p;
        Env env = new Env();

        for (Def def : prog.listdef_) {
            def.accept(new Def.Visitor<Object, Env>() {
                @Override
                public Object visit(DFun f, Env env) {
                    List<TypeCode> args = new LinkedList<>();
                    for (Arg arg : f.listarg_) {
                        ADecl a = arg.accept(new CheckArg(), env);
                        args.add(TypeCoder.fromType(a.type_));
                    }
                    env.addFun(f.id_, new FunType(args, TypeCoder.fromType(f.type_)));

                    return null;
                }
            }, env);
        }

        for (Def def : prog.listdef_) {
            def.accept(new Def.Visitor<Object, Env>() {
                @Override
                public Object visit(DFun f, Env env) {
                    checkFun(f, env);
                    return null;
                }
            }, env);
        }
    }

    private void checkFun(DFun f, Env env) {
        env.enterFunction(f.id_);

        for (Arg arg : f.listarg_) {
            ADecl a = arg.accept(new CheckArg(), env);
            env.addVar(a.id_, TypeCoder.fromType(a.type_));
        }

        for (Stm st : f.liststm_) {
            st.accept(new CheckStm(), env);
        }

        env.leaveFunction();
    }

    private class CheckStm implements Stm.Visitor<Object,Env> {
        @Override
        public Object visit(SExp stm, Env env) {
            TypeInferrer.inferExp(stm.exp_, env);
            return null;
        }

        @Override
        public Object visit(SDecls stm, Env env) {
            for (String id : stm.listid_) {
                env.addVar(id, TypeCoder.fromType(stm.type_));
            }
            return null;
        }

        @Override
        public Object visit(SInit stm, Env env) {
            TypeCode t = TypeCoder.fromType(stm.type_);
            env.addVar(stm.id_, t);
            TypeInferrer.checkExp(stm.exp_, t, env);
            return null;
        }

        @Override
        public Object visit(SReturn stm, Env env) {
            TypeCode et = TypeInferrer.inferExp(stm.exp_, env);
            TypeCode t = env.getReturnType();
            if (et != t) {
                throw TypeException.returnType(stm, et, env);
            }
            return null;
        }

        @Override
        public Object visit(SReturnVoid stm, Env env) {
            TypeCode et = TypeCode.VOID;
            TypeCode t = env.getReturnType();
            if (et != t) {
                throw TypeException.returnType(stm, et, env);
            }
            return null;
        }

        @Override
        public Object visit(SWhile stm, Env env) {
            TypeCode et = TypeInferrer.inferExp(stm.exp_, env);
            TypeCode t = TypeCode.BOOL;
            if (et != t) {
                throw TypeException.expressionCondition(stm.exp_, et, env);
            }

            stm.stm_.accept(this, env);
            return null;
        }

        @Override
        public Object visit(SBlock stm, Env env) {
            env.enterScope();
            for (Stm st : stm.liststm_) {
                st.accept(this, env);
            }
            env.leaveScope();
            return null;
        }

        @Override
        public Object visit(SIfElse stm, Env env) {
            TypeCode et = TypeInferrer.inferExp(stm.exp_, env);
            TypeCode t = TypeCode.BOOL;
            if (et != t) {
                throw TypeException.expressionCondition(stm.exp_, et, env);
            }

            stm.stm_1.accept(this, env);
            stm.stm_2.accept(this, env);
            return null;
        }
    }

    private class CheckArg implements Arg.Visitor<ADecl, Env> {
        @Override
        public ADecl visit(ADecl a, Env env) {
            return a;
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
