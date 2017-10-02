package larc.ludiconprod;

import android.support.v4.app.INotificationSideChannel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;

/**
 * Created by alex_ on 19.09.2017.
 */

public class UserProfile extends User {
    public static final Comparator<String> MONTH_COMP = new Comparator<String>() {
        private HashMap<String, Integer> mi = new HashMap<>();
        {
            mi.put("jan", 0);
            mi.put("feb", 1);
            mi.put("mar", 2);
            mi.put("apr", 3);
            mi.put("may", 4);
            mi.put("jun", 5);
            mi.put("jul", 6);
            mi.put("aug", 7);
            mi.put("sep", 8);
            mi.put("oct", 9);
            mi.put("nov", 10);
            mi.put("dec", 11);
        }

        @Override
        public int compare(String a, String b) {
            a = a.toLowerCase();
            b = b.toLowerCase();
            Integer av = mi.get(a);
            Integer bv = mi.get(b);
            if (av == null || bv == null) {
                return 0;
            }
            return av - bv;
        }
    };

    public TreeMap<String, Integer> pointsM = new TreeMap<>(UserProfile.MONTH_COMP);
    public TreeMap<String, Integer> eventsM = new TreeMap<>(UserProfile.MONTH_COMP);
    public int events;
}
