package larc.ludicon.UserInfo;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Ciprian on 11/18/2015.
 */
public class User {

    private static String mfilename = "UserDetails";
    private static String mUserId = "Id";
    public static final String firstName = "firstName";
    public static final String lastName= "lastName";
    public static final String email= "email";

    public static final String sex = "unknown";
    public static final Date birthDate = new Date();

    /* Important: We get the data in these static fields, but after log in
    our user is "parsUser" */
    public static ParseUser parseUser;

    public static final String password = "pass";

    /* This function will fill the user info from parse */
    public static void updateUserFromParse(Context context){

    }

    public static void setPassword(String i_password, Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString(password, i_password);
        edit.commit();
    }
    public static String getPassword(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        return sharedPref.getString(password, "");
    }

    public static String getId(Context context) {
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

    public static void setInfo(String fName, String lName, String id ,String mail,Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString(mUserId, id);
        edit.putString(firstName, fName);
        edit.putString(lastName, lName);
        edit.putString(email,mail);
        edit.commit();
    }

    public static void clear(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename,0);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.clear().commit();
    }


}
