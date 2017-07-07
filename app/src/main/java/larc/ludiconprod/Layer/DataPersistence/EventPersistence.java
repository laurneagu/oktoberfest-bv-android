package larc.ludiconprod.Layer.DataPersistence;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import larc.ludiconprod.Utils.Event;

/**
 * Created by LaurUser on 7/5/2017.
 */

public class EventPersistence {

    final static  String userDetailsString = "UserDetails";
    final static String eventHappeningNowString = "currentEvent";
    final static String eventHappeningNowStartedDateString = "eventStartedAt";

    final static String eventsString = "events";
    final static String myEventsString = "myEvents";
    final static String eventHappeningNowStateCheck = "currentEventStateCheck";

    // Singleton
    private static EventPersistence instance = null;
    protected EventPersistence() {}

    public static EventPersistence getInstance() {
        if(instance == null) {
            instance = new EventPersistence();
        }
        return instance;
    }

    public void persistCurrentEvent(Activity activity, Event eventInformation) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(userDetailsString, 0).edit();

        Gson gson = new Gson();
        String json = gson.toJson(eventInformation);
        editor.putString(eventHappeningNowString, json);
        editor.commit();
    }

    public Event getCurrentEvent(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userDetailsString, 0);

        Gson gson = new Gson();
        String json = sharedPreferences.getString(eventHappeningNowString, "");
        if (json != null) {
            Event currentEvent = gson.fromJson(json, Event.class);
            return currentEvent;
        }
        return null;
    }

    // States of current event:
    // 0 - didn't start, 1 - started, 2 - stopped, 3 - nothing
    public String getCurrentEventState(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userDetailsString, 0);

        String currentEventState = sharedPreferences.getString(eventHappeningNowStateCheck, "3");

        return currentEventState;
    }

    public void setCurrentEventState(Activity activity, String newState) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userDetailsString, 0);
        sharedPreferences.edit().putString(eventHappeningNowStateCheck, newState).commit();
    }

    public String getCurrentEventDateStarted(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userDetailsString, 0);
        return sharedPreferences.getString(eventHappeningNowStartedDateString, "0");
    }

    public void setCurrentEventDateStarted(Activity activity, Date date) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userDetailsString, 0);
        sharedPreferences.edit().putString(eventHappeningNowStartedDateString, date.toString()).commit();
    }

    public void cleanUpEvents(Activity activity){
        SharedPreferences.Editor editor = activity.getSharedPreferences(userDetailsString, 0).edit();

        String connectionsJSONString = new Gson().toJson(null);
        editor.putString(eventsString, connectionsJSONString);
        editor.commit();
    }

    public List<Event> getEvents(Activity activity){
        SharedPreferences editor = activity.getSharedPreferences(userDetailsString, 0);
        String json = editor.getString(eventsString, "");
        Type type = new TypeToken<List<Event>>() {}.getType();
        Gson gson = new Gson();

        List<Event> events = null;

        if (json != null) {
            events = gson.fromJson(json, type);
        }
        return events;
    }

    public void setMyEvents(Activity activity, List<Event> myEvents){
        SharedPreferences.Editor editor = activity.getSharedPreferences(userDetailsString, 0).edit();
        Gson gson = new Gson();

        editor.putString(myEventsString, gson.toJson(myEvents));
        editor.commit();
    }


}
