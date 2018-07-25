package larc.oktoberfestprod.Utils.util;
import java.security.SecureRandom;
import java.math.BigInteger;
/**
 * Created by LaurUser on 2/13/2016.
 */
public class UniqueIDCreator {
    private SecureRandom random = new SecureRandom();

    public String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }
}
