package CPP;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by mkaay on 31.05.15.
 */
class Env {
    private static Map<String, FunType> signatures;
    private List<Map<String, TypeCode>> scopes;
    private static FunType function;

    public Env() {
        scopes = new LinkedList<>();
        function = null;
        enterScope();
    }

    public TypeCode lookupVar(String x) {
        for (Map<String, TypeCode> scope : scopes) {
            TypeCode t = scope.get(x);
            if (t != null) {
                return t;
            }
        }
        throw new TypeException(String.format("Unknown variable %s.", x));
    }

    public static FunType lookupFun(String id) {
        if (signatures.containsKey(id)) {
            return signatures.get(id);
        }
        throw new TypeException(String.format("Unknown function %s.", id));
    }

    public void addVar(String x, TypeCode t) {
        if (scopes.get(0).containsKey(x)) {
            throw new TypeException(String.format("Variable %s is already declared in this scope.", x));
        }
        scopes.get(0).put(x, t);
    }

    public void addFun(String id, FunType t) {
        if (signatures.containsKey(id)) {
            throw new TypeException(String.format("Function %s is already declared in this scope.", id));
        }
        signatures.put(id, t);
    }

    public void enterScope() {
        scopes.add(0, new HashMap<String, TypeCode>());
    }

    public void leaveScope() {
        scopes.remove(0);
    }

    public void enterFunction(String id) {
        function = signatures.get(id);
    }

    public void leaveFunction() {
        function = null;
    }

    public TypeCode getReturnType() {
        assert function != null;
        return function.val;
    }
}
