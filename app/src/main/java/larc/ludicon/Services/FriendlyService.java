package larc.ludicon.Services;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import larc.ludicon.Utils.Location.GPSTracker;
import larc.ludicon.Utils.Location.ServiceLocationListener;
import larc.ludicon.UserInfo.ActivityInfo;
import larc.ludicon.Utils.util.ChatNotifier;
import larc.ludicon.Utils.util.DateManager;
import larc.ludicon.Utils.util.Notifier;



/**
 * Created by Andrei on 2/27/2016.
 */

public class FriendlyService extends Service {

    public static int maxDistanceMeters = 500; //m

    public static final long MIN = 60 * 1000;

    // Time to notify (in minutes)
    private static int limitTime = 20;

    private boolean mRunning;

    private static ChatNotifier chatNotifier = new ChatNotifier();
    private static Notifier notifier = new Notifier();

    private LocalBroadcastManager broadcastManager=null;

    private Object waitForNextEvent = new Object();


    /* LOCATION */
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000*60*1;
    private static final float LOCATION_DISTANCE = 10f;

    private class LocationListener implements android.location.LocationListener{
        //Location mLastLocation;
        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            //mLastLocation = new Location(provider);
        }
        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            //mLastLocation.set(location);
        }
        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER)
    };

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private Location getLocationOnlyOnce(){

        try {

            initializeLocationManager();

            try {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[0]);
                if (mLocationManager != null) {

                    try {
                        Location location = mLocationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location != null) {


                            mLocationManager.removeUpdates(mLocationListeners[0]);
                            return location;
                        }
                    } catch (java.lang.SecurityException ex) {
                        Log.i(TAG, "fail to request location update, ignore", ex);
                    }
                }

            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "gps provider does not exist " + ex.getMessage());
            }

        }catch(Exception e){
            // magic
        }

        return null;
    }


    /*** LOCATION END */

    private boolean isLocationOk(){
        //Looper.prepare();
        Location current = getLocationOnlyOnce();
        if(current != null){
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            //ref.child("mesg").child("service").child("alive").setValue(current.getLatitude() + " - " + current.getLongitude() + new Date().toString());

            String json = getSharedPreferences("UserDetails", 0).getString("currentEvent", "");
            Gson gson = new Gson();
            ActivityInfo currentEvent = gson.fromJson(json, ActivityInfo.class);

            if ( currentEvent != null ) {
                Location targetLocation = new Location("");//provider name is unecessary
                targetLocation.setLatitude(currentEvent.latitude);//your coords of course
                targetLocation.setLongitude(currentEvent.longitude);
                double distance = current.distanceTo(targetLocation);
                ref.child("mesg").child("service").child("distance").setValue(distance +"   " + new Date().toString());
                if( distance <= maxDistanceMeters){
                    // TODO custom maxDistance by Event/Event Location
                    return true;
                }
                else {
                    return false;
                }
            }

        }
        else{
            return false;
        }

        return true;
    }

    private BroadcastReceiver receiveStartRequest = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();


            Intent resp = new Intent("ServiceToMain_StartResponse");
            if(isLocationOk()) {
                resp.putExtra("Response", "0");
                getSharedPreferences("UserDetails", 0).edit().putString("currentEventState", "1").commit(); // started

                // Notify thread to start counting
                synchronized (waitForNextEvent) {
                        waitForNextEvent.notify();
                }
            }
            else{
                resp.putExtra("Response", "1");
            }


            if(broadcastManager != null)
                broadcastManager.sendBroadcast(resp);
        }
    };

    public FriendlyService(){
        try{
            broadcastManager = LocalBroadcastManager.getInstance(this);
            broadcastManager.registerReceiver(
                    receiveStartRequest, new IntentFilter("MainToService_StartRequest"));


        }
        catch(Exception e){
            broadcastManager=null;
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Laur For test !!
       // getSharedPreferences("UserDetails", 0).edit().putBoolean("isServiceRunning",false).commit();

       // mRunning =  getSharedPreferences("UserDetails", 0).getBoolean("isServiceRunning",false);

        //if( mRunning == false) {
        mRunning = true;
        //initializeLocationManager();

        getSharedPreferences("UserDetails", 0).edit().putBoolean("isServiceRunning",true).commit();

        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
        getSharedPreferences("UserDetails", 0).edit().putString("currentEventIsActive", "0").commit();
        ref1.child("mesg").child("service").child("currentEventIsActive").setValue("0___" + new Date().toString());


        // Thread for Rewarding user with points - ADIOS
//        Runnable eventParticipationChecker = getCheckPointsThread();
//        Thread eventParticipationThread = new Thread(eventParticipationChecker);
//        eventParticipationThread.start();

        // Thread for Notifying user for upcoming events
        Runnable eventChecker = getCheckNotificationsEventsThread();
        Thread eventCheckerThread = new Thread(eventChecker);
        eventCheckerThread.start();

        // Thread for Notifying user new chat message
        Runnable chatChecker = getCheckNotificationsChatThread();
        Thread chatCheckerThread = new Thread(chatChecker);
        chatCheckerThread.start();

        // Check Alive - Testing
//        Runnable aliveCheck = getCheckAliveThread(getApplicationContext(),this);
//        Thread aliveCheckThread = new Thread(aliveCheck);
//        aliveCheckThread.start();

                // Persistent Run:
        //return Service.START_STICKY;
        //}

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
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                while (true) {

//                    ref.child("mesg").child("service").child("whileBIG").setValue("here"+new Date().toString());

                    // Get the list of events from Shared Prefs
                    String connectionsJSONString = getSharedPreferences("UserDetails", 0).getString("events", null);
                    Type type = new TypeToken<List<ActivityInfo>>() {}.getType();
                    List<ActivityInfo> events = null;
                    if (connectionsJSONString != null) {
                        events = new Gson().fromJson(connectionsJSONString, type);
                    }


                    if (events != null && events.size() != 0) {

                        getSharedPreferences("UserDetails", 0).edit().putString("currentEventIsActive", "0").commit();
//                        ref.child("mesg").child("service").child("currentEventIsActive").setValue("0___" + new Date().toString());


                        // Get current date
                        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale("English"));
                        //df.setTimeZone(TimeZone.getTimeZone("gmt"));
                        df.setTimeZone(TimeZone.getTimeZone("Europe/Bucharest"));
                        String gmtTime = df.format(new Date());
                        Date now = new Date(gmtTime);
//                        ref.child("mesg").child("service").child("whileBIGTimeNOW").setValue(now.toString());

                        Log.v("Date now",now.toString());
                        // Problem - TimeZone

                        // Check if event is about to happen in the next 20 min
                        Date limit = new Date(now.getTime() + limitTime * MIN);

                        // Get the current pending event
                        int j = 0;
                        int jj = 0;
                        while (j < events.size()){
                            if (events.get(j).date!=null) {
                                if (now.after(events.get(j).date))
                                    j++;
                                else break;
                            }
                            // else nu are cum
                        }

                        ActivityInfo upcomingEvent;

                        if (j == events.size()) upcomingEvent = events.get(j - 1);
                        else upcomingEvent = events.get(j);

                        Log.v("Date.now",now.toString());
                        Log.v("Upcoming Event", upcomingEvent.date.toString());

//                        ref.child("mesg").child("service").child("upcommingEventDate").setValue(upcomingEvent.date.toString());

                        // Pending event starts in the next 20 min
                        if (upcomingEvent.date.before(limit) && now.before(upcomingEvent.date)) {
                            long diff = upcomingEvent.date.getTime() - now.getTime();
                            long diffMinutes = diff / (60 * 1000) % 60;

//                            ref.child("mesg").child("service").child("upcommingEventDateIn20min").setValue("here");

                            // Less then 10 minutes til event
                            if (diffMinutes <= 10) {
                                //ref.child("mesg").child("service").child("upcommingEventDateIn10min").setValue("here");

                                Log.v("Event-Service","Event is in less then 10  min");
                                int diffMin = (int) diffMinutes;

                                // Sleep till the start of the event ( 1 minute error )
                                if (diffMin > 1) {
                                    try {
                                        Thread.sleep((diffMin - 1) * MIN, 100);
                                    } catch (InterruptedException exc) {
                                    }
                                }



                                // SAVE IN SHARED PREFS THE PENDING EVENT
                                SharedPreferences.Editor editor = getSharedPreferences("UserDetails", 0).edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(upcomingEvent); // Type is activity info
                                editor.putString("currentEvent", json);
                                editor.commit();

                                getSharedPreferences("UserDetails", 0).edit().putString("currentEventIsActive", "1").commit();
//                                ref.child("mesg").child("service").child("currentEventIsActive").setValue("1___" + new Date().toString());
                                Date limitPending = new Date(new Date().getTime() + 5 * MIN); // 5 min pending


                                SharedPreferences.Editor editor1 = getSharedPreferences("UserDetails", 0).edit();
                                Gson gson1 = new Gson();
                                String json1 = gson.toJson(upcomingEvent); // Type is activity info
                                editor.putString("HappeningNowEvent", json);
                                editor.commit();

                                // Right here the event is about to start
                                // Notify MainActivity to show happening now panel:
                                getSharedPreferences("UserDetails", 0).edit().putString("currentEventState", "0").commit(); // pending
                                Intent intent = new Intent("ServiceToMain_ReceiveIsHappening");
                                intent.putExtra("isActive", "0");
                                if(broadcastManager != null)
                                    broadcastManager.sendBroadcast(intent);

                                // Wait until the current event is started
                                synchronized (waitForNextEvent) {
                                    try {
                                        waitForNextEvent.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                // Event started, count points:
                                int eventPoints = 0, locationErrors = 0;
                                while(true){

                                    String state = getSharedPreferences("UserDetails", 0).getString("currentEventState", "2");

                                    // If the user didn't press stop, event is started
                                    if (Integer.parseInt(state) == 1) { // started
                                        if(isLocationOk()){
                                            // set points

                                            eventPoints+=1;// TODO add custom amount of points based on event priority

                                            // over two hours
                                            if(eventPoints > 24){

                                                // TODO STOP
                                            }

                                        }
                                        else{ // bad location

                                        }
                                    }

                                    else{ // stopped
                                        Intent intent2 = new Intent("ServiceToMain_ReceiveIsHappening");
                                        intent2.putExtra("isActive", "1");
                                        if(broadcastManager != null)
                                            broadcastManager.sendBroadcast(intent2);
                                        break;
                                    }

                                    try {
                                        Thread.sleep(5*MIN);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }
                                getSharedPreferences("UserDetails", 0).edit().putString("currentEvent", "").commit();
                                getSharedPreferences("UserDetails", 0).edit().putString("HappeningNowEvent", "").commit();
                                // Write points:
                                // Delete current event from shared preferences when ended

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
                        Thread.sleep(1 * MIN);
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
                        Thread.sleep(MIN/4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
    }

    private Runnable getCheckAliveThread(final Context cnt, final Service srv){
        return new Runnable() {
            public void run() {
                Looper.prepare();
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("mesg").child("service").child("alive").setValue("RESTART!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                while(true) {

                    //isLocationOk();
                    try {
                        Thread.sleep(10000);
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

                //DatabaseReference.setAndroidContext(getApplicationContext());
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                String refJson = getSharedPreferences("UserDetails", 0).getString("uid", null);

                SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
                final String myName = prefs.getString("username", null);

                if (refJson == null) return;

                final DatabaseReference userRef = ref.child("users").child(refJson);
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
                                                String d = DateManager.convertFromSecondsToText((long)msgData.getValue());
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

                                                SimpleDateFormat form = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
                                                chatNotifier.sendNotification(FriendlyService.this, getSystemService(NOTIFICATION_SERVICE), getResources(), getNotificationIndex(), author, message, form.format(date), chatUid);
                                            }
                                            //userRef.child("chatNotif").setValue(author);
                                            // see it!
                                            ref.child("chat").child(chatUid).child("Messages").child(snapshot.getKey()).child("seen").setValue(true);
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
                                    public void onCancelled(DatabaseError firebaseError) {

                                    }

                                });
                            } else {
                                // TODO the conversation was deleted
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
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

        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (java.lang.SecurityException ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




}



