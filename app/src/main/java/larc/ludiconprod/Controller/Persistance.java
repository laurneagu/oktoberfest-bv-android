package larc.ludiconprod.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import larc.ludiconprod.Activities.BalanceActivity;
import larc.ludiconprod.User;
import larc.ludiconprod.Utils.Chat;
import larc.ludiconprod.Utils.Coupon;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.HappeningNowLocation;

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
    private final String balanceString = "BalanceActivity";
    private final String cuponsString = "CouponsActivityCoupons";
    private final String myCuponsString = "CouponsActivityMyCoupons";
    private final String locationString = "locationsList";
    private final String conversationString="conversationList";

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


    public void setConversation(Activity activity, ArrayList<Chat> chatList){
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.conversationString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.conversationString, gson.toJson(chatList));
        editor.commit();
    }

    public ArrayList<Chat> getConversation(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(this.conversationString, 0);
        String json = sharedPreferences.getString(this.conversationString, "0");
        ArrayList<Chat> chatList = new ArrayList<>();;

        Type type = new TypeToken<ArrayList<Chat>>() {
        }.getType();
        if (json.equals("0")) {
            chatList = new ArrayList<>();
        } else {
            chatList = new Gson().fromJson(json, type);
        }
        return  chatList;
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
        ArrayList<Event>eventList=new ArrayList<>();
        Type type = new TypeToken<ArrayList<Event>>() {
        }.getType();
        if (json.equals("0")) {
            eventList = new ArrayList<>();
        } else {
            eventList = gson.fromJson(json, type);
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

    public ArrayList<BalanceActivity.BalanceEntry> getBalanceCache(Activity activity) {
        String json = null;
        System.out.println(activity + "activity");
        SharedPreferences sharedPreferences = activity.getSharedPreferences(balanceString, 0);
        json = sharedPreferences.getString(balanceString, "0");
        Gson gson = new Gson();
        User user;
        if (json.equals("0")) {
            return new ArrayList<>();
        }
        return gson.fromJson(json, ArrayList.class);
    }

    public void setBalanceCache(ArrayList<BalanceActivity.BalanceEntry> entries, Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.balanceString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.balanceString, gson.toJson(entries));
        editor.commit();
    }
    public void setLocation(Activity activity, HappeningNowLocation locationList){
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.locationString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.locationString, gson.toJson(locationList));
        editor.commit();
    }

    public HappeningNowLocation getLocation(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(this.locationString, 0);
        String json = sharedPreferences.getString(this.locationString, "0");
        HappeningNowLocation locationsList;

        if (json.equals("0")) {
            locationsList = new HappeningNowLocation();
        } else {
            locationsList = new Gson().fromJson(json, HappeningNowLocation.class);
        }
        return  locationsList;
    }

    public ArrayList<Coupon> getCouponsCache(Activity activity) {
        System.out.println(activity + "activity");
        SharedPreferences sharedPreferences = activity.getSharedPreferences(cuponsString, 0);
        String json = sharedPreferences.getString(cuponsString, "0");
        Gson gson = new Gson();
        if (json.equals("0")) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Coupon>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void setCouponsCache(ArrayList<Coupon> cs, Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.cuponsString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.cuponsString, gson.toJson(cs));
        editor.commit();
    }

    public ArrayList<Coupon> getMyCouponsCache(Activity activity) {
        System.out.println(activity + "activity");
        SharedPreferences sharedPreferences = activity.getSharedPreferences(myCuponsString, 0);
        String json = sharedPreferences.getString(myCuponsString, "0");
        Gson gson = new Gson();
        if (json.equals("0")) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Coupon>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void setMyCouponsCache(ArrayList<Coupon> cs, Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.myCuponsString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.myCuponsString, gson.toJson(cs));
        editor.commit();
    }
}
