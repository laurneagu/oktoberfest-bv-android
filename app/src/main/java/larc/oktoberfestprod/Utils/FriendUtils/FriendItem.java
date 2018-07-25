package larc.oktoberfestprod.Utils.FriendUtils;
import java.util.*;
import java.util.Comparator;

/**
 * Created by LaurUser on 12/11/2016.
 */
public class FriendItem implements Comparator, Comparable<FriendItem>{
        public String name;
        public String uid;
        public String ImageUrl;
        public String numberOfSports;
        public String friendsString;


    @Override
    public int compare(Object lhs, Object rhs) {
        String name1 = ((FriendItem) lhs).name;
        String name2 = ((FriendItem) rhs).name;

        if (name1.compareToIgnoreCase(name2) > 0) {
            return -1;
        } else if (name1.compareToIgnoreCase(name2) < 0){
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(FriendItem another) {
        String name1 = this.name;
        String name2 = another.name;

        if (name1.compareToIgnoreCase(name2) > 0) {
            return 1;
        } else if (name1.compareToIgnoreCase(name2) < 0){
            return -1;
        } else {
            return 0;
        }
    }
}
