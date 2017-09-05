package larc.ludiconprod.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import larc.ludiconprod.User;
import larc.ludiconprod.Utils.Event;

/**
 * Created by ancuta on 7/12/2017.
 */

public class Persistance {
    private static Persistance instance = null;

    protected Persistance() {
    }

    public static Persistance getInstance() {
        if (instance == null) {
            instance = new Persistance();
        }
        return instance;
    }

    private final String userDetailsString = "UserDetails";
    private final String profileDetailsString = "ProfileDetails";
    private final String myActivitiesString="MyActivities";
    private final String aroundMeActivitiesString="AroundMeActivities";

    public void setUserInfo(Activity activity, User user) {

        SharedPreferences.Editor editor = activity.getSharedPreferences(userDetailsString, 0).edit();
        Gson gson = new Gson();
        editor.putString(userDetailsString, gson.toJson(user));
        editor.commit();
    }

    public void setUnseenChats(Context activity, ArrayList<String> chatList){
        SharedPreferences.Editor editor = activity.getSharedPreferences("UnseenChats", 0).edit();
        Gson gson = new Gson();
        editor.putString("UnseenChats", gson.toJson(chatList));
        editor.commit();
    }

    public ArrayList<String> getUnseenChats(Context activity){
        String json = null;
        SharedPreferences sharedPreferences = activity.getSharedPreferences("UnseenChats", 0);
        json = sharedPreferences.getString("UnseenChats", "0");
        Gson gson = new Gson();
        ArrayList<String> chatList;
        if (json.equals("0")) {
            chatList = new ArrayList<>();
        } else {
            chatList = gson.fromJson(json, ArrayList.class);
        }
        return chatList;
    }


    public User getUserInfo(Activity activity) {
        String json = null;
        System.out.println(activity + "activity");
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userDetailsString, 0);
        json = sharedPreferences.getString(userDetailsString, "0");
        Gson gson = new Gson();
        User user;
        if (json.equals("0")) {
            user = new User();
        } else {
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
    public void setMyActivities(Activity activity, ArrayList<Event> eventList){
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.myActivitiesString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.myActivitiesString, gson.toJson(eventList));
        editor.commit();
    }

    public ArrayList<Event> getMyActivities(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(this.myActivitiesString, 0);
        String json = sharedPreferences.getString(this.myActivitiesString, "0");
        ArrayList<Event> eventList = new ArrayList<>();;

        Type type = new TypeToken<ArrayList<Event>>() {
        }.getType();
        if (json.equals("0")) {
            eventList = new ArrayList<>();
        } else {
            eventList = new Gson().fromJson(json, type);
        }
        return  eventList;
    }

    public void setAroundMeActivities(Activity activity, ArrayList<Event> eventList){
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.aroundMeActivitiesString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.aroundMeActivitiesString, gson.toJson(eventList));
        editor.commit();
    }

    public ArrayList<Event> getAroundMeActivities(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(this.aroundMeActivitiesString, 0);
        String json = sharedPreferences.getString(this.aroundMeActivitiesString, "0");
        Gson gson = new Gson();
        ArrayList<Event> eventList;
        if (json.equals("0")) {
            eventList = new ArrayList<>();
        } else {
            eventList = gson.fromJson(json, ArrayList.class);
        }
        return  eventList;
    }

    public User getProfileInfo(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(this.profileDetailsString, 0);
        String json = sharedPreferences.getString(this.profileDetailsString, "0");
        Gson gson = new Gson();
        User user;
        if (json.equals("0")) {
            user = new User();
        } else {
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
        LoginManager.getInstance().logOut();
    }
}
