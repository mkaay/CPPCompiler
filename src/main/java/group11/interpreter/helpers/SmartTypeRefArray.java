package group11.interpreter.helpers;

import org.robovm.llvm.binding.TypeRef;
import org.robovm.llvm.binding.TypeRefArray;

public class SmartTypeRefArray extends TypeRefArray {
    private int current = 0;
    private int max;

    public SmartTypeRefArray(int nelements) {
        super(nelements);
        max = nelements;
    }

    public void add(TypeRef value) {
        if (current >= max) {
            throw new ArrayIndexOutOfBoundsException();
        }

        set(current++, value);
    }
}
