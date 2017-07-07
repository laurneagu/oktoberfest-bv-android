package larc.ludiconprod.Layer.DataPersistence;

import android.app.Activity;
import android.content.SharedPreferences;

import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.Location.LocationInfo;

/**
 * Created by LaurUser on 7/5/2017.
 */

public class UserPersistence {
    final static String userDetailsString = "UserDetails";
    final static String uidString = "uid";

    // Singleton
    private static UserPersistence instance = null;
    protected UserPersistence() {
    }
    public static UserPersistence getInstance() {
        if(instance == null) {
            instance = new UserPersistence();
        }
        return instance;
    }

    public void persistUserID(Activity activity, String userID) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(userDetailsString, 0).edit();

        editor.putString(uidString, userID);
        editor.commit();
    }

}
