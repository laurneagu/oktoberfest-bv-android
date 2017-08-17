package larc.ludiconprod.Controller;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import larc.ludiconprod.User;

/**
 * Created by ancuta on 7/12/2017.
 */

public class Persistance {
    private static Persistance instance = null;

    protected Persistance() {
    }

    public static Persistance getInstance() {
        if(instance == null) {
            instance = new Persistance();
        }
        return instance;
    }
    private final String userDetailsString = "UserDetails";
    private final String profileDetailsString="ProfileDetails";

    public void setUserInfo(Activity activity, User user){

        SharedPreferences.Editor editor = activity.getSharedPreferences(userDetailsString, 0).edit();
        Gson gson = new Gson();
        editor.putString(userDetailsString, gson.toJson(user));
        editor.commit();
    }


    public User getUserInfo(Activity activity){
        String json=null;
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userDetailsString, 0);
        json = sharedPreferences.getString(userDetailsString, "0");
        Gson gson = new Gson();
        User user;
        if(json.equals("0")){
            user=new User();
        }
        else {
            user = gson.fromJson(json, User.class);
        }
        return user;
    }

    public void setProfileInfo(Activity activity, User user) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.profileDetailsString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.profileDetailsString, gson.toJson(user));
        editor.commit();
    }

    public User getProfileInfo(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(this.profileDetailsString, 0);
        String json = sharedPreferences.getString(this.profileDetailsString, "0");
        Gson gson = new Gson();
        User user;
        if(json.equals("0")){
            user=new User();
        }
        else {
            user = gson.fromJson(json, User.class);
        }
        return user;
    }

    public void deleteUserProfileInfo(Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.profileDetailsString, 0).edit();
        editor.clear();
        editor.commit();
        editor = activity.getSharedPreferences(this.userDetailsString, 0).edit();
        editor.clear();
        editor.commit();
    }
}
