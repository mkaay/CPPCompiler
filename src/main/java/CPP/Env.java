package CPP;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by mkaay on 31.05.15.
 */
class Env {
    private Map<String, FunType> signatures;
    private LinkedList<Map<String, TypeCode>> scopes;
    private FunType function;

    public Env() {
        scopes = new LinkedList<>();
        signatures = new HashMap<>();
        function = null;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("Signatures:");
        s.append("\n");

        for (Map.Entry<String, FunType> pair : signatures.entrySet()) {
            s.append(pair.getValue().val);
            s.append(" ");
            s.append(pair.getKey());
            s.append("(");
            for (TypeCode t : pair.getValue().args) {
                s.append(t);
                s.append(",");
            }
            s.append(")");
            s.append("\n");
        }

        s.append("\n");

        s.append("Context:");
        s.append("\n");

        for (Map<String, TypeCode> ctx : scopes) {
            s.append("-start-");
            s.append("\n");

            for (Map.Entry<String, TypeCode> pair : ctx.entrySet()) {
                s.append(pair.getValue());
                s.append(" ");
                s.append(pair.getKey());
                s.append("\n");
            }

            s.append("-end-");
            s.append("\n");
        }

        return s.toString();
    }

    public TypeCode lookupVar(String x) {
        for (Map<String, TypeCode> scope : scopes) {
            TypeCode t = scope.get(x);
            if (t != null) {
                return t;
            }
        }
        throw new TypeException(String.format("Unknown variable %s.", x), this);
    }

    public FunType lookupFun(String id) {
        if (signatures.containsKey(id)) {
            return signatures.get(id);
        }
        throw new TypeException(String.format("Unknown function %s.", id), this);
    }

    public void addVar(String x, TypeCode t) {
        if (scopes.getFirst().containsKey(x)) {
            throw new TypeException(String.format("Variable %s is already declared in this scope.", x), this);
        }
        scopes.getFirst().put(x, t);
    }

    public void addFun(String id, FunType t) {
        if (signatures.containsKey(id)) {
            throw new TypeException(String.format("Function %s is already declared in this scope.", id), this);
        }
        signatures.put(id, t);
    }

    public void enterScope() {
        scopes.addFirst(new HashMap<String, TypeCode>());
    }

    public void leaveScope() {
        scopes.removeFirst();
    }

    public void enterFunction(String id) {
        function = signatures.get(id);
        enterScope();
    }

    public void leaveFunction() {
        function = null;
        leaveScope();
    }

    public TypeCode getReturnType() {
        assert function != null;
        return function.val;
    }
}
