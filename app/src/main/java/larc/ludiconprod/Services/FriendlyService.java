package larc.ludiconprod.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import larc.ludiconprod.Activities.ChatListActivity;
import larc.ludiconprod.Activities.ChatTemplateActivity;
import larc.ludiconprod.ChatUtils.Chat;
import larc.ludiconprod.UserInfo.ActivityInfo;
import larc.ludiconprod.Utils.util.ChatNotifier;
import larc.ludiconprod.Utils.util.DateManager;
import larc.ludiconprod.Utils.util.Notifier;


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
            broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
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
                    try {
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
                            Thread.sleep(MIN / 4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }catch(Exception e){
                        try {
                            Thread.sleep(MIN / 6);
                        } catch (InterruptedException ee) {
                            ee.printStackTrace();
                        }
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
                try{
                //DatabaseReference.setAndroidContext(getApplicationContext());
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                String refJson = getSharedPreferences("UserDetails", 0).getString("uid", null);

                SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
                final String myName = prefs.getString("username", null);

                if (refJson == null) return;

                final DatabaseReference userRef = ref.child("users").child(refJson);
                final LinkedList<String> chatRefs = new LinkedList<String>(); // chat uid - last message date

                final SharedPreferences sharedPref = getApplication().getSharedPreferences("ChatMessages", 0);
                // Listen new 1 to 1 conversations
                userRef.child("chats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            //userRef.child("chatNotif"+i).setValue((String)data.getValue());
                            final String chatUid = (String) data.getValue();
                            if (chatRefs.contains(chatUid) == false) {

                                chatRefs.add(chatUid);

                                // Listen new messages and notify
                                ref.child("chat").child(chatUid).child("Messages").addChildEventListener(new ChildEventListener() {
                                    // Retrieve new posts as they are added to the database
                                    @Override
                                    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                                        String author = "";
                                        String message = "";
                                        Date date = null;
                                        String seen = "false";

                                        chatRefs.add(chatUid);

                                        for (DataSnapshot msgData : snapshot.getChildren()) {
                                            if (msgData.getKey().toString().equalsIgnoreCase("author")) {
                                                author = msgData.getValue().toString();
                                            }

                                            if (msgData.getKey().toString().equalsIgnoreCase("date")) {

                                                try {
                                                    String d = DateManager.convertFromSecondsToText((long) msgData.getValue());
                                                    String res = d.split(",")[1];
                                                    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm");
                                                    date = format.parse(res);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }

                                            if (msgData.getKey().toString().equalsIgnoreCase("message")) {
                                                message = msgData.getValue().toString();
                                            }

                                            if (msgData.getKey().toString().equalsIgnoreCase("seen")) {
                                                seen = msgData.getValue().toString();
                                            }
                                        }

                                        if (myName.compareToIgnoreCase(author) == 0) {
                                            return;
                                        }

                                        if (message.compareToIgnoreCase("Welcome to our chat! :)") == 0){
                                            return;
                                        }

                                        boolean notAlreadySent = false;
                                        Long dateMilis = Long.valueOf(sharedPref.getString(chatUid,"0"));
                                        Date lastNotifDate = new Date(dateMilis);
                                        if(lastNotifDate.before(date))
                                        {
                                            notAlreadySent = true;
                                            sharedPref.edit().putString(chatUid, date.getTime() + "").commit();
                                        }
                                        // It is not my message
                                        // If I haven't seen it
                                        if (notAlreadySent && seen == "false") {
                                            if (!isForeground("larc.ludiconprod")) { // if chat is not open
                                                if(date != null) {
                                                    SimpleDateFormat form = new SimpleDateFormat("dd MMM, HH:mm");
                                                    chatNotifier.sendNotification(FriendlyService.this, getSystemService(NOTIFICATION_SERVICE), getResources(), getNotificationIndex(), author, message, form.format(date), chatUid, false);
                                                }else{
                                                    chatNotifier.sendNotification(FriendlyService.this, getSystemService(NOTIFICATION_SERVICE), getResources(), getNotificationIndex(), author, message, "", chatUid, false);
                                                }
                                            }
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

                    final LinkedList<String> eventChatRefs = new LinkedList<String>(); // chat uid - last message date

                    // Listen new event conversations
                    userRef.child("events").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                final String eventUid = (String) data.getKey();

                                    // Listen new messages and notify
                                    ref.child("events").child(eventUid).child("chat").addChildEventListener(new ChildEventListener() {
                                        // Retrieve new posts as they are added to the database
                                        @Override
                                        public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                                            String author = "";
                                            String message = "";
                                            long date = 0;

                                            chatRefs.add(eventUid);

                                            for (DataSnapshot msgData : snapshot.getChildren()) {
                                                if (msgData.getKey().toString().equalsIgnoreCase("author")) {
                                                    author = msgData.getValue().toString();
                                                }

                                                if (msgData.getKey().toString().equalsIgnoreCase("date")) {
                                                    date = Long.parseLong(msgData.getValue().toString());
                                                }

                                                if (msgData.getKey().toString().equalsIgnoreCase("message")) {
                                                    message = msgData.getValue().toString();
                                                }
                                            }

                                            if (myName.compareToIgnoreCase(author) == 0) {
                                                return;
                                            }
                                            if (message.compareToIgnoreCase("Welcome to our chat! :)") == 0){
                                                return;
                                            }


                                            // It is not my message
                                                Log.v("Name vs Author", myName + " " + author);//Creating a shared preference

                                            SharedPreferences  mPrefs = getApplicationContext().getSharedPreferences("serviceData", MODE_PRIVATE);
                                            Gson gson = new Gson();
                                            String json = mPrefs.getString(eventUid,"");
                                            Chat lastChatMsg;
                                            boolean isNewMsg = false;
                                            if (!json.equalsIgnoreCase("")) {
                                                lastChatMsg = gson.fromJson(json, Chat.class);
                                                if (lastChatMsg.date < date)
                                                    isNewMsg = true;
                                            }
                                            else
                                                isNewMsg = true;

                                                if (!isForeground("larc.ludiconprod") && isNewMsg) { // if chat is not open
                                                    String dateString = DateManager.convertFromSecondsToText(date);
                                                    String resultDateString = dateString.split(",")[1].trim();

                                                    chatNotifier.sendNotification(FriendlyService.this, getSystemService(NOTIFICATION_SERVICE), getResources(), getNotificationIndex(), author, message, resultDateString, eventUid, true);
                                                    json = gson.toJson(new Chat(message,author,date));
                                                    mPrefs.edit().putString(eventUid, json).commit();
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
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                        }
                    });

            }catch(Exception e){
                    try {
                        Thread.sleep(MIN / 4);
                    } catch (InterruptedException ee) {
                        ee.printStackTrace();
                    }
                    run();
                }
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



