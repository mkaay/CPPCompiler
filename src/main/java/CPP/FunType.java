package CPP;

import java.util.List;

/**
 * Created by mkaay on 31.05.15.
 */
public class FunType {
    public List<TypeCode> args;
    public TypeCode val;

    public FunType(List<TypeCode> args, TypeCode val) {
        this.args = args;
        this.val = val;
    }
}
