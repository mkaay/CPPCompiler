package group11.interpreter.helpers;

import group11.interpreter.InterpreterException;
import org.robovm.llvm.binding.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class FunctionScope {
    private final ValueRef function;
    private final BuilderRef allocator;

    private final LinkedList<BasicBlockRef> blocks = new LinkedList<>();
    private BuilderRef builder = null;
    private final LinkedList<Map<String, Variable>> lookupTable = new LinkedList<>();

    private Map<String, Integer> prefixCounter = new HashMap<>();


    public FunctionScope(ValueRef function) {
        this.function = function;

        allocator = LLVM.CreateBuilder();
        LLVM.PositionBuilderAtEnd(allocator, LLVM.AppendBasicBlock(function, "allocate"));

        createBlock("entry");
        enterScope();
    }

    public BuilderRef getBuilder() {
        return builder;
    }

    private void refreshBuilder() {
        if (builder != null) {
            LLVM.DisposeBuilder(builder);
        }

        builder = LLVM.CreateBuilder();
        LLVM.PositionBuilderAtEnd(builder, blocks.getFirst());
    }

    public BasicBlockRef createBlock(String name) {
        return LLVM.AppendBasicBlock(function, name);
    }

    public BasicBlockRef enterBlock(BasicBlockRef block) {
        blocks.addFirst(block);

        refreshBuilder();

        return block;
    }

    public void leaveBlock() {
        if (blocks.size() < 1) {
            throw new InterpreterException("trying to leave entry block");
        }

        blocks.removeFirst();
        refreshBuilder();
    }

    public void enterScope() {
        lookupTable.addFirst(new HashMap<String, Variable>());
    }

    public void leaveScope() {
        Map<String, Variable> scope = lookupTable.getFirst();

        for (Variable variable : scope.values()) {
            //LLVM.BuildFree(builder, variable.value);
        }

        lookupTable.removeFirst();
    }

    public Variable getVariable(String name) {
        for (Map<String, Variable> scope : lookupTable) {
            for (Map.Entry<String, Variable> entry : scope.entrySet()) {
                if (entry.getKey().equals(name)) {
                    return entry.getValue();
                }
            }
        }

        throw new InterpreterException(String.format("can't find '%s' in lookup table", name));
    }

    public ValueRef addVariable(String name, TypeRef type) {
        ValueRef value = LLVM.BuildAlloca(allocator, type, uniqueName(name));
        lookupTable.getFirst().put(name, new Variable(type, value));
        return value;
    }

    public String uniqueName(String name) {
        return String.format("%s_%d", name, getPrefixCount(name));
    }

    private int getPrefixCount(String prefix) {
        int count = prefixCounter.getOrDefault(prefix, 0);
        prefixCounter.put(prefix, count + 1);
        return count;
    }
}
