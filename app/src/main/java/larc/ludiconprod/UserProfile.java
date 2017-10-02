package larc.ludiconprod;

import android.support.v4.app.INotificationSideChannel;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;

/**
 * Created by alex_ on 19.09.2017.
 */

public class UserProfile extends User {
    private static final HashMap<String, Integer> SORTED_MONTHS = new HashMap<>();
    static {
        HashMap<String, Integer> mi = UserProfile.SORTED_MONTHS;
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
    public static final Comparator<String> MONTH_COMP = new Comparator<String>() {
        @Override
        public int compare(String a, String b) {
            a = a.toLowerCase();
            b = b.toLowerCase();
            Integer av = UserProfile.SORTED_MONTHS.get(a);
            Integer bv = UserProfile.SORTED_MONTHS.get(b);
            if (av == null || bv == null) {
                return 0;
            }
            return av - bv;
        }
    };

    public static void sortMonths(ArrayList<String> months) {
        Collections.sort(months, UserProfile.MONTH_COMP);
        if (months.contains("Jan") && months.contains("Dec")) {
            ArrayList<String> sm = new ArrayList<>(SORTED_MONTHS.keySet());
            Collections.sort(sm, MONTH_COMP);

            int index = 0;
            while (true) {
                String f = months.remove(0);
                ++index;
                months.add(f);
                if (!months.get(0).equalsIgnoreCase(sm.get(index))) {
                    break;
                }
            }
        }
    }

    public TreeMap<String, Integer> pointsM = new TreeMap<>(UserProfile.MONTH_COMP);
    public TreeMap<String, Integer> eventsM = new TreeMap<>(UserProfile.MONTH_COMP);
    public int events;
}
