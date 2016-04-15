package larc.ludicon.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import larc.ludicon.Activities.FriendsActivity;
import larc.ludicon.Utils.Location.ServiceLocationListener;
import larc.ludicon.UserInfo.ActivityInfo;
import larc.ludicon.Utils.util.Notifier;

/**
 * Created by Andrei on 2/27/2016.
 * REV HISTORY:
 *  10APR 2016: Laur Neagu
 *      Service purpose : Notify event is close to happen (30 min) + Reward player with points
 *      Refactor the code of the service
 */

public class FriendlyService extends Service {

    public static final long MIN = 60*1000;

    // Time to notify (in minutes)
    private static int limitTime = 30;

    private LocationManager mLocationManager;
    private ServiceLocationListener mLocationListener = new ServiceLocationListener(this);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initializeLocationManager();
        mLocationListener.requestUpdates(mLocationManager);

        // Thread for Rewarding user with points
        Runnable eventParticipationChecker = new Runnable() {
            public void run() {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while(true){

                    // Get the list of events from Shared Prefs
                    String connectionsJSONString = getSharedPreferences("UserDetails", 0).getString("events", null);
                    Type type = new TypeToken< List<ActivityInfo>>() {}.getType();
                    List < ActivityInfo > events = null;
                    if(connectionsJSONString != null) {
                        events = new Gson().fromJson(connectionsJSONString, type);
                    }

                    if(events != null){

                        // Get current date
                        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale("English"));
                        df.setTimeZone(TimeZone.getTimeZone("gmt"));
                        String gmtTime = df.format(new Date());
                        Date now = new Date(gmtTime);

                        Date limit = new Date(now.getTime() + limitTime * MIN);

                        // Get the current pending event
                        int j = 0;
                        while ( j < events.size() && now.after(events.get(j).date)  ) j++;

                        ActivityInfo upcomingEvent;
                        if ( j == events.size() ) upcomingEvent = events.get(j - 1);
                        else upcomingEvent = events.get(j);

                        // Pending event starts in the next limitTime minutes - default 30
                        if ( upcomingEvent.date.before(limit) && now.before(upcomingEvent.date) )
                        {
                            long diff = upcomingEvent.date.getTime() - now.getTime();
                            long diffMinutes = diff / (60 * 1000) % 60;

                            // Less then 10 minutes til event
                            if ( diffMinutes <= 10 )
                            {
                                int diffMin = (int)diffMinutes;
                                if ( diffMin > 1 ) {
                                    try {
                                        Thread.sleep((diffMin - 1) * 1000, 1);
                                    } catch (InterruptedException exc) {
                                    }
                                }

                                // sleep till the start of the event ( 1 minute error )

                                // Point where the event starts
                                final ArrayList<Integer> pointsList = new ArrayList<>();

                                int eventPoints = 0,locationErrors = 0;
                                // Count points if he has not left the location and if has not scored yet 10 points
                                while (locationErrors <= 3 && eventPoints <= 10)
                                {

                                    // Get last known location from SharedPref
                                    SharedPreferences sharedPrefs = getSharedPreferences("UserDetails",0);
                                    double latitude = Double.parseDouble(sharedPrefs.getString("latitude", "0"));
                                    double longitude = Double.parseDouble(sharedPrefs.getString("longitude", "0"));

                                    // New coordinates received
                                    if ( latitude != 0 || longitude != 0)
                                    {
                                        // Get distance between current location and event location
                                        float[] distance = new float[10];
                                        Location.distanceBetween(latitude,longitude, upcomingEvent.latitude, upcomingEvent.longitude, distance);

                                        Log.v("distance1", mLocationListener.getLatitude() + " " + mLocationListener.getLongitude() + " vs " + upcomingEvent.latitude + " " + upcomingEvent.longitude);
                                        Log.v("distance2", distance[0] + " ");

                                        // User left the location
                                        if ( distance[0] >= 500 ) {
                                            locationErrors++;
                                        }

                                        // User is in location ( difference < 500 m ) add points to the eventPoints
                                        else{
                                            eventPoints++;
                                        }
                                    }
                                    // From 10 to 10 minutes, recheck the user is still there
                                    try{
                                        Thread.sleep(600000,1);
                                    }
                                    catch(InterruptedException exc){}
                                }
                                // Update points with the ones received for the current event and update in Database
                                // Check if there are unsaved points in SharedPrefs
                                // Points are tied to events in a Map<Event_ID,Event_Points>
                                Map<String,Integer> unsavedPointsMap = new HashMap<>();
                                SharedPreferences pSharedPref = getSharedPreferences("Points", Context.MODE_PRIVATE);
                                try{
                                    if (pSharedPref != null){
                                        String jsonString = pSharedPref.getString("UnsavedPointsMap", (new JSONObject()).toString());
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        Iterator<String> keysItr = jsonObject.keys();
                                        while(keysItr.hasNext()) {
                                            String key = keysItr.next();
                                            Integer value = (Integer) jsonObject.get(key);
                                            unsavedPointsMap.put(key, value);
                                        }
                                    }
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                // Add the current event points to SharedPref
                                unsavedPointsMap.put(upcomingEvent.id,eventPoints);
                                if (pSharedPref != null){
                                    JSONObject jsonObject = new JSONObject(unsavedPointsMap);
                                    String jsonString = jsonObject.toString();
                                    SharedPreferences.Editor editor = pSharedPref.edit();
                                    editor.remove("UnsavedPointsMap").commit();
                                    editor.putString("UnsavedPointsMap", jsonString);
                                    editor.commit();
                                }
                            }
                        }
                    }

                    // Sleep limitTime minutes
                    try {
                        Thread.sleep(limitTime*MIN);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        Thread eventParticipationThread = new Thread(eventParticipationChecker);
        eventParticipationThread.start();

        // Thread for Notifying user for upcoming events
        Runnable eventChecker = new Runnable() {
            public void run() {
                // LAUR - Is this really needed ?
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int i =0;
                Map<Date,Integer> checkNotSent30 = new HashMap<Date,Integer>();
                while(true){

                    String connectionsJSONString = getSharedPreferences("UserDetails", 0).getString("events", null);
                    Type type = new TypeToken< List<ActivityInfo>>() {}.getType();
                    List < ActivityInfo > events = null;
                    if(connectionsJSONString != null) {
                        events = new Gson().fromJson(connectionsJSONString, type);
                    }
                    if(events == null){
                       // User.firebaseRef.child("mesgEvents").setValue("Nu are evenimente");
                    }
                    else{
                        //User.firebaseRef.child("mesgEvents").setValue("Are evenimente : " + events.get(events.size()-1).date);
                       // User.firebaseRef.child("mesgEventsNrEvenimente").setValue("NR: " + events.size());

                        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale("English"));
                        df.setTimeZone(TimeZone.getTimeZone("gmt"));
                        String gmtTime = df.format(new Date());

                        Date now = new Date(gmtTime);
                        Date aux = new Date(gmtTime);
                        //User.firebaseRef.child("mesgEventsNow").setValue(now.toString());
                        Date limit = new Date(aux.getTime() + 30 * MIN);
                        //User.firebaseRef.child("mesgEventsLimit").setValue(limit.toString());

                        for(ActivityInfo ai : events){
                           if(ai.date != null && ai.date.after(now) && ai.date.before(limit) && !checkNotSent30.containsKey(ai.date)){
                               checkNotSent30.put(ai.date, i);
                               Notifier notifier = new Notifier();

                               notifier.sendNotification(FriendlyService.this, getSystemService(NOTIFICATION_SERVICE), getResources(), i, 30, ai.sport, ai.others, ai.place, ai.date);

                               i++;
                               if(i == Integer.MAX_VALUE -1){
                                   i = 0;
                               }
                           }
                        }
                    }


                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        Thread eventCheckerThread = new Thread(eventChecker);
        eventCheckerThread.start();

        // Persistent Run:
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null && mLocationListener != null) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (SecurityException ex) {
                }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}



