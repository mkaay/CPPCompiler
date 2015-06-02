package group11.typechecker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

class Env {
    private Map<String, FunType> signatures;
    private LinkedList<Map<String, TypeCode>> scopes;
    private String function;

    public Env() {
        scopes = new LinkedList<>();
        signatures = new HashMap<>();
        function = null;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("<Env>\n");

        s.append("<Signatures>\n");

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

        s.append("</Signatures>\n");

        s.append("<Context>\n");

        int depth = scopes.size();

        while (depth-- > 0) {
            s.append("{");
            s.append("\n");
        }

        for (Map<String, TypeCode> ctx : scopes) {
            for (Map.Entry<String, TypeCode> pair : ctx.entrySet()) {
                s.append(pair.getValue());
                s.append(" ");
                s.append(pair.getKey());
                s.append("\n");
            }

            s.append("}");
            s.append("\n");
        }

        s.append("</Context>\n");
        s.append("</Env>\n");

        return s.toString();
    }

    public TypeCode lookupVar(String x) {
        for (Map<String, TypeCode> scope : scopes) {
            TypeCode t = scope.get(x);
            if (t != null) {
                return t;
            }
        }
        throw TypeException.variableUnknown(x, this);
    }

    public FunType lookupFun(String id) {
        if (signatures.containsKey(id)) {
            return signatures.get(id);
        }
        throw TypeException.functionUnknown(id, this);
    }

    public void addVar(String x, TypeCode t) {
        if (scopes.getFirst().containsKey(x)) {
            throw TypeException.variableExists(x, this);
        }
        scopes.getFirst().put(x, t);
    }

    public void addFun(String id, FunType t) {
        if (signatures.containsKey(id)) {
            throw TypeException.functionExists(id, this);
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
        function = id;
        enterScope();
    }

    public void leaveFunction() {
        function = null;
        leaveScope();
    }

    public TypeCode getReturnType() {
        assert function != null;
        return signatures.get(function).val;
    }

    public String getFunctionName() {
        assert function != null;
        return function;
    }
}
