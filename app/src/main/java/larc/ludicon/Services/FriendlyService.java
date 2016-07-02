package larc.ludicon.Services;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import larc.ludicon.Activities.ChatListActivity;
import larc.ludicon.Activities.ChatTemplateActivity;
import larc.ludicon.Activities.FriendsActivity;
import larc.ludicon.UserInfo.User;
import larc.ludicon.Utils.Location.ServiceLocationListener;
import larc.ludicon.UserInfo.ActivityInfo;
import larc.ludicon.Utils.util.ChatNotifier;
import larc.ludicon.Utils.util.Notifier;



/**
 * Created by Andrei on 2/27/2016.
 * REV HISTORY:
 *  10APR 2016: Laur Neagu
 *      Service purpose : Notify event is close to happen (30 min) + Reward player with points
 *      Refactor the code of the service
 */

public class FriendlyService extends Service {

    public static final long MIN = 60 * 1000;

    // Time to notify (in minutes)
    private static int limitTime = 20;

    private LocationManager mLocationManager;
    private ServiceLocationListener mLocationListener = new ServiceLocationListener(this);
    private boolean mRunning;

    private static ChatNotifier chatNotifier = new ChatNotifier();
    private static Notifier notifier = new Notifier();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Laur For test !!
        getSharedPreferences("UserDetails", 0).edit().putBoolean("isServiceRunning",false).commit();

        mRunning =  getSharedPreferences("UserDetails", 0).getBoolean("isServiceRunning",false);

        if( mRunning == false) {
            mRunning = true;
            //initializeLocationManager();

            getSharedPreferences("UserDetails", 0).edit().putBoolean("isServiceRunning",true).commit();

            getSharedPreferences("UserDetails", 0).edit().putString("currentEventIsActive","0").commit();

            // Thread for Rewarding user with points
            Runnable eventParticipationChecker = getCheckPointsThread();
            Thread eventParticipationThread = new Thread(eventParticipationChecker);
            eventParticipationThread.start();

            // Thread for Notifying user for upcoming events
            Runnable eventChecker = getCheckNotificationsEventsThread();
            Thread eventCheckerThread = new Thread(eventChecker);
            eventCheckerThread.start();

            // Thread for Notifying user new chat message
            Runnable chatChecker = getCheckNotificationsChatThread();
            Thread chatCheckerThread = new Thread(chatChecker);
            chatCheckerThread.start();

            // Persistent Run:
            return Service.START_STICKY;
        }
        // Persistent Run:
        return Service.START_STICKY;
    }

    private Runnable getCheckPointsThread(){
        return new Runnable() {
            public void run() {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (true) {

                    // Get the list of events from Shared Prefs
                    String connectionsJSONString = getSharedPreferences("UserDetails", 0).getString("events", null);
                    Type type = new TypeToken<List<ActivityInfo>>() {}.getType();
                    List<ActivityInfo> events = null;
                    if (connectionsJSONString != null) {
                        events = new Gson().fromJson(connectionsJSONString, type);
                    }

                    if (events != null) {

                        getSharedPreferences("UserDetails", 0).edit().putString("currentEventIsActive","0").commit();
                        // Get current date
                        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale("English"));
                        //df.setTimeZone(TimeZone.getTimeZone("gmt"));
                        df.setTimeZone(TimeZone.getTimeZone("Europe/Bucharest"));
                        String gmtTime = df.format(new Date());
                        Date now = new Date(gmtTime);
                        Log.v("Date now",now.toString());
                        // Problem - TimeZone

                        // Check if event is about to happen in the next 20 min
                        Date limit = new Date(now.getTime() + limitTime * MIN);

                        // Get the current pending event
                        int j = 0;
                        while (j < events.size()){
                            if (events.get(j).date!=null)
                                if(now.after(events.get(j).date))  j++;
                        }

                        ActivityInfo upcomingEvent;
                        if (j == events.size()) upcomingEvent = events.get(j - 1);
                        else upcomingEvent = events.get(j);

                        Log.v("Date.now",now.toString());
                        Log.v("Upcoming Event",upcomingEvent.date.toString());

                        // Pending event starts in the next 20 min
                        if (upcomingEvent.date.before(limit) && now.before(upcomingEvent.date)) {
                            long diff = upcomingEvent.date.getTime() - now.getTime();
                            long diffMinutes = diff / (60 * 1000) % 60;

                            // Less then 10 minutes til event
                            if (diffMinutes <= 10) {

                                Log.v("Event-Service","Event is in less then 10  min");
                                int diffMin = (int) diffMinutes;

                                // Sleep till the start of the event ( 1 minute error )
                                if (diffMin > 1) {
                                    try {
                                        Thread.sleep((diffMin - 1) * MIN, 100);
                                    } catch (InterruptedException exc) {
                                    }
                                }

                                getSharedPreferences("UserDetails", 0).edit().putString("currentEventIsActive","1").commit();

                                // SAVE IN SHARED PREFS THE PENDING EVENT
                                SharedPreferences.Editor editor = getSharedPreferences("UserDetails", 0).edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(upcomingEvent); // Type is activity info
                                editor.putString("currentEvent", json);
                                editor.commit();

                                // Point where the event starts
                                final ArrayList<Integer> pointsList = new ArrayList<>();

                                int eventPoints = 0, locationErrors = 0;

                                // Count points if he has not left the location and if has not scored yet 10 points
                                while (locationErrors <= 3 && eventPoints <= 10) {

                                    // Start Location Listener
                                    initializeLocationManager();
                                    // Request location updates
                                    mLocationListener.requestUpdates(mLocationManager);

                                    // Get last known location from SharedPref
                                    SharedPreferences sharedPrefs = getSharedPreferences("UserDetails", 0);
                                    double latitude = Double.parseDouble(sharedPrefs.getString("latitude", "0"));
                                    double longitude = Double.parseDouble(sharedPrefs.getString("longitude", "0"));

                                    // New coordinates received
                                    if (latitude != 0 || longitude != 0) {
                                        // Get distance between current location and event location
                                        float[] distance = new float[10];
                                        Location.distanceBetween(latitude, longitude, upcomingEvent.latitude, upcomingEvent.longitude, distance);

                                        Log.v("distance1", mLocationListener.getLatitude() + " " + mLocationListener.getLongitude() + " vs " + upcomingEvent.latitude + " " + upcomingEvent.longitude);
                                        Log.v("distance2", distance[0] + " ");

                                        // User left the location
                                        if (distance[0] >= 500) {
                                            locationErrors++;
                                            Log.v("Location-Service","User is not in location and has errors: " + locationErrors);
                                        }

                                        // User is in location ( difference < 500 m ) add points to the eventPoints
                                        else {
                                            eventPoints++;
                                            Log.v("Location-Service","User is in location and has: " + eventPoints);
                                        }
                                    }

                                    //  Remove location listener
                                    //removeUpdatesLocationManager();

                                    // From 10 to 10 minutes, recheck the user is still there
                                    try {
                                        Thread.sleep(1 * MIN, 1);
                                    } catch (InterruptedException exc) {
                                    }
                                }

                                // Delete current event from shared preferences when ended
                                getSharedPreferences("UserDetails", 0).edit().putString("currentEvent", "").commit();
                                getSharedPreferences("UserDetails", 0).edit().putString("currentEventIsActive","0").commit();

                                // Update points with the ones received for the current event and update in Database
                                // Check if there are unsaved points in SharedPrefs
                                // Points are tied to events in a Map<Event_ID,Event_Points>
                                Map<String, Integer> unsavedPointsMap = new HashMap<>();
                                SharedPreferences pSharedPref = getSharedPreferences("Points", Context.MODE_PRIVATE);
                                try {
                                    if (pSharedPref != null) {
                                        String jsonString = pSharedPref.getString("UnsavedPointsMap", (new JSONObject()).toString());
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        Iterator<String> keysItr = jsonObject.keys();
                                        while (keysItr.hasNext()) {
                                            String key = keysItr.next();
                                            Integer value = (Integer) jsonObject.get(key);
                                            unsavedPointsMap.put(key, value);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                // Add the current event points to SharedPref
                                unsavedPointsMap.put(upcomingEvent.id, eventPoints);
                                if (pSharedPref != null) {
                                    JSONObject jsonObject = new JSONObject(unsavedPointsMap);
                                    String jsonString = jsonObject.toString();
                                    editor = pSharedPref.edit();
                                    editor.remove("UnsavedPointsMap").commit();
                                    editor.putString("UnsavedPointsMap", jsonString);
                                    editor.commit();
                                }
                            }
                        }

                    }

                    Log.v("Ludicon", "i am here !");

                    try {
                        // Sleep 10 min
                        Thread.sleep(2 * MIN);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
    }

    private Runnable getCheckNotificationsEventsThread(){
        return new Runnable() {
            public void run() {
                // LAUR - Is this really needed ?
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int i = 0;
                Map<Date, Integer> checkNotSent30 = new HashMap<Date, Integer>();
                while (true) {

                    String connectionsJSONString = getSharedPreferences("UserDetails", 0).getString("events", null);
                    Type type = new TypeToken<List<ActivityInfo>>() {
                    }.getType();
                    List<ActivityInfo> events = null;
                    if (connectionsJSONString != null) {
                        events = new Gson().fromJson(connectionsJSONString, type);
                    }
                    if (events == null) {
                        // User.firebaseRef.child("mesgEvents").setValue("Nu are evenimente");
                    } else {
                        //User.firebaseRef.child("mesgEvents").setValue("Are evenimente : " + events.get(events.size()-1).date);
                        // User.firebaseRef.child("mesgEventsNrEvenimente").setValue("NR: " + events.size());

                        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale("English"));
                        df.setTimeZone(TimeZone.getTimeZone("Europe/Bucharest"));
                        String gmtTime = df.format(new Date());

                        Date now = new Date(gmtTime);
                        Date aux = new Date(gmtTime);
                        //User.firebaseRef.child("mesgEventsNow").setValue(now.toString());
                        Date limit = new Date(aux.getTime() + 30 * MIN);
                        //User.firebaseRef.child("mesgEventsLimit").setValue(limit.toString());

                        for (ActivityInfo ai : events) {
                            if (ai.date != null && ai.date.after(now) && ai.date.before(limit) && !checkNotSent30.containsKey(ai.date)) {
                                checkNotSent30.put(ai.date, i);

                                notifier.sendNotification(FriendlyService.this, getSystemService(NOTIFICATION_SERVICE), getResources(), i, 30, ai.sport, ai.others, ai.place, ai.date);

                                i++;
                                if (i == Integer.MAX_VALUE - 1) {
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
    }

    public boolean isForeground(String myPackage) {
//        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
//        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
//        return componentInfo.getPackageName().equals(myPackage);
        try{
            if(ChatListActivity.isForeground || ChatTemplateActivity.isForeground){
                return true;
            }
            else {
                return false;
            }
        }catch (Exception e){
            return false;
        }

    }

    public int getNotificationIndex(){
        synchronized (chatNotifier.lock){
            if(chatNotifier.chatNotificationIndex >= 5) {
                chatNotifier.deleteNotification(getSystemService(NOTIFICATION_SERVICE), chatNotifier.chatNotificationFirstIndex);
                chatNotifier.chatNotificationFirstIndex++;
            }
            return chatNotifier.chatNotificationIndex++;
        }
    }

    private Runnable getCheckNotificationsChatThread(){
        return new Runnable() {
            public void run() {

                Firebase.setAndroidContext(getApplicationContext());
                final Firebase ref = new Firebase("https://ludicon.firebaseio.com/");
                String refJson = getSharedPreferences("UserDetails", 0).getString("uid", null);

                SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
                final String myName = prefs.getString("username", null);

                final Firebase userRef = ref.child("users").child(refJson);
                final LinkedList<String> chatRefs = new LinkedList<String>(); // chat uid - last message date

                // Listen new conversation
                userRef.child("chats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot data: snapshot.getChildren()) {
                            //userRef.child("chatNotif"+i).setValue((String)data.getValue());
                            final String chatUid = (String) data.getValue();
                            if (chatRefs.contains(chatUid) == false) {

                                chatRefs.add(chatUid);

                                // Listen new messages and notify
                                ref.child("chat").child(chatUid).child("Messages").addChildEventListener(new ChildEventListener() {
                                    // Retrieve new posts as they are added to the database
                                    @Override
                                    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                                        String author="";
                                        String message="";
                                        Date date = new Date();
                                        String seen = "false";

                                        chatRefs.add(chatUid);

                                        for (DataSnapshot msgData: snapshot.getChildren()) {
                                            if (msgData.getKey().toString().equalsIgnoreCase("author")){
                                                author = msgData.getValue().toString();
                                            }

                                            if (msgData.getKey().toString().equalsIgnoreCase("date")){
                                                String d = msgData.getValue().toString();
                                                try {

                                                    SimpleDateFormat format = new SimpleDateFormat("MMMM dd, HH:mm",Locale.ENGLISH);
                                                    date = format.parse(d);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            if (msgData.getKey().toString().equalsIgnoreCase("message")){
                                                message = msgData.getValue().toString();
                                            }

                                            if (msgData.getKey().toString().equalsIgnoreCase("seen")){
                                                seen = msgData.getValue().toString();
                                            }
                                        }

                                        if(myName.compareToIgnoreCase(author) == 0){
                                            return;
                                        }

                                        // It is not my message
                                        // If I haven't seen it
                                        if(seen == "false"){
                                             //Notification !!!
                                            Log.v("Name vs Author", myName + " " + author);
                                            if(!isForeground("larc.ludicon")){ // if chat is not open


                                                chatNotifier.sendNotification(FriendlyService.this, getSystemService(NOTIFICATION_SERVICE), getResources(), getNotificationIndex(), author, message, date);
                                            }
                                            //userRef.child("chatNotif").setValue(author);
                                            // see it!
                                            ref.child("chat").child(chatUid).child("Messages").child(snapshot.getKey()).child("seen").setValue("true");
                                        }


                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }

                                });
                            } else {
                                // TODO the conversation was deleted
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });


            }
        };
    }

    @Override
    public void onCreate() {

        super.onCreate();
        mRunning = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeUpdatesLocationManager();
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

    private void removeUpdatesLocationManager() {
        if (mLocationManager != null && mLocationListener != null) {
            try {
                mLocationManager.removeUpdates(mLocationListener);
            } catch (SecurityException ex) {
            }
        }
    }

}



