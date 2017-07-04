package larc.ludiconprod.Utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by LaurUser on 7/4/2017.
 */

public class General {

    public static int getDayOfMonth(Date aDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
}
