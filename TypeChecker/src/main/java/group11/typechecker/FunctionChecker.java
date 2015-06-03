package group11.typechecker;

import CPP.Absyn.*;

public class FunctionChecker implements Def.Visitor<Object, Environment> {
    public Object visit(DFun f, Environment env) {
        env.enterFunction(f.id_);

        for (Arg arg : f.listarg_) {
            ADecl a = arg.accept(new ArgumentChecker(), env);
            env.addVariable(a.id_, TypeCoder.fromType(a.type_));
        }

        for (Stm st : f.liststm_) {
            st.accept(new StatementChecker(), env);
        }

        env.leaveFunction();

        return null;
    }
}
