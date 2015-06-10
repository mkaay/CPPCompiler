package group11.interpreter.helpers;

import org.robovm.llvm.binding.ValueRef;
import org.robovm.llvm.binding.ValueRefArray;

public class SmartValueRefArray extends ValueRefArray {
    private int current = 0;
    private int max;

    public SmartValueRefArray(int nelements) {
        super(nelements);
        max = nelements;
    }

    public void add(ValueRef value) {
        if (current >= max) {
            throw new ArrayIndexOutOfBoundsException();
        }

        set(current++, value);
    }
}
