package larc.ludicon.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ciprian on 11/18/2015.
 */
public class UserIdSave {
    private static String mfilename = "UserDetails";
    private static String mUserId = "Id";
    public static final String firstName = "firstName";
    public static final String lastName= "lastName";
    public static final String email= "email";

    public static String getIdFromSharedPref(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        String jsonDataString = sharedPref.getString(mUserId, "");
        return jsonDataString;
    }
    public static String getFirstName(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        return sharedPref.getString(firstName, "");
    }
    public static String getLastName(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        return sharedPref.getString(lastName, "");
    }
    public static String getEmail(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        return sharedPref.getString(email, "");
    }
    public static void setInfotoSharedPref(String fName, String lName, String id ,String mail,Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString(mUserId, id);
        edit.putString(firstName, fName);
        edit.putString(lastName, lName);
        edit.putString(email,mail);
        edit.commit();
    }

    public static void clearFromSharedPref(Context context) {

        SharedPreferences sharedPref = context.getSharedPreferences(mfilename,0);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.clear();
    }


}
