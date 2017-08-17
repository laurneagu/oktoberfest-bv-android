package larc.ludiconprod.Utils;

import java.util.HashMap;

import larc.ludiconprod.User;

@Deprecated
public class GlobalResources {
    private static final HashMap<Object, GlobalResources> resource = new HashMap<>();
    private final User data = new User();

    public User getData() {
        return data;
    }

    public static GlobalResources getInstance(Object o) {
        GlobalResources res = GlobalResources.resource.get(o);
        if (res == null) {
            res = new GlobalResources();
            GlobalResources.resource.put(o, res);
        }
        return res;
    }
}
