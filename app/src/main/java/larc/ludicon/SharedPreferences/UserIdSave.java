package larc.ludicon.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ciprian on 11/18/2015.
 */
public class UserIdSave {

    private static String mfilename = "UserDetails";
    private static String mUserId = "Id";

    public static String getIdFromSharedPref(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        String jsonDataString = sharedPref.getString(mUserId, "");
        return jsonDataString;
    }

    public static void setIdToSharedPref(String toAdd, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString(mUserId, toAdd);
        edit.commit();
    }

}
