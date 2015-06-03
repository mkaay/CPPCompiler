package group11.typechecker;

import CPP.Absyn.ADecl;
import CPP.Absyn.Arg;

class ArgumentChecker implements Arg.Visitor<ADecl, Environment> {
    @Override
    public ADecl visit(ADecl a, Environment env) {
        return a;
    }
}
