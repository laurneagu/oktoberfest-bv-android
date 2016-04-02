package larc.ludicon.Utils.util;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;


import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import larc.ludicon.Activities.IntroActivity;
import larc.ludicon.Activities.MainActivity;
import larc.ludicon.R;
import larc.ludicon.UserInfo.ActivityInfo;
import larc.ludicon.UserInfo.User;
import larc.ludicon.Utils.Location_GPS.GPS_Positioning;
import larc.ludicon.Utils.Location_GPS.MyLocationListener;

/**
 * Created by Andrei on 2/27/2016.
 */

public class BackgroundService extends Service {

    public static final long MIN = 60*1000;
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 60000;
    private static final float LOCATION_DISTANCE = 10f;

    private class LocationListener implements android.location.LocationListener{
        Location mLastLocation;
        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }
        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            HashMap<String,String> map = new HashMap<>();
            map.put("latitude",location.getLatitude()+"");
            map.put("longitude", location.getLongitude() + "");
            User.firebaseRef.child("users").child(User.uid).child("location").setValue(map);
        }
        @Override
        public void onProviderDisabled(String provider)
        {
            //Log.e(TAG, "onProviderDisabled: " + provider);
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            //Log.e(TAG, "onProviderEnabled: " + provider);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            //Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    void sendNotification(int notificationNumber, int minutesToEvent, String sport, int otherPlayers , String place, Date date){
        NotificationManager manager;
        Notification myNotification;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //Intent intent = new Intent("com.rj.notitfications.SECACTIVITY");
        Intent intent = new Intent(this, IntroActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(BackgroundService.this, 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(BackgroundService.this);

        builder.setAutoCancel(false);
        builder.setTicker("You have one activity in " + minutesToEvent + " minutes");

        builder.setContentTitle(sport + " activity remainder");
        builder.setContentText("In less than " + minutesToEvent + " minutes you will play " + sport.toLowerCase() + " with " + otherPlayers + " other player" + (otherPlayers != 1 ? "s" : "") + " at " + place);
        builder.setSmallIcon(R.drawable.logo);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        builder.setLargeIcon(largeIcon);

        builder.setContentIntent(pendingIntent);
        //builder.setOngoing(true);
        builder.setSubText("Have fun!");   //API level 16
        //builder.setNumber(number * 100);
        builder.build();

        myNotification = builder.getNotification();
        manager.notify(notificationNumber, myNotification);
    }

    public void requestUpdates(LocationListener [] mLocationListeners, LocationManager mLocationManager)
    {
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initializeLocationManager();
        requestUpdates(mLocationListeners,mLocationManager);
        Runnable eventParticipationChecker = new Runnable() {
            public void run() {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

               int limitTime = 30;
                try{
                    Thread.sleep(100,1);
                }
                catch(InterruptedException exc){}

                while(true){
                    // take
                    String connectionsJSONString = getSharedPreferences("UserDetails", 0).getString("events", null);
                    Type type = new TypeToken< List<ActivityInfo>>() {}.getType();
                    List < ActivityInfo > events = null;
                    if(connectionsJSONString != null) {
                        events = new Gson().fromJson(connectionsJSONString, type);
                    }
                    if(events == null){
                    }
                    else{

                        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale("English"));
                        df.setTimeZone(TimeZone.getTimeZone("gmt"));
                        String gmtTime = df.format(new Date());

                        Date now = new Date(gmtTime);
                        Date aux = new Date(gmtTime);
                        Date limit = new Date(aux.getTime() + limitTime * MIN);

                        int j = 0;
                        while ( j < events.size() && now.after(events.get(j).date)  ) j++;
                        ActivityInfo upcomingEvent;
                        if ( j == events.size() ) upcomingEvent = events.get(j - 1);
                        else upcomingEvent = events.get(j);

                        if ( upcomingEvent.date.before(limit) && now.before(upcomingEvent.date) )
                        {
                            long diff = upcomingEvent.date.getTime() - now.getTime();
                            long diffMinutes = diff / (60 * 1000) % 60;
                            User.firebaseRef.child("mesgEvents").setValue("Mai sunt " + diffMinutes + " minute pana la eventul urmator");

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
                                // event starts

                                final ArrayList<Integer> pointsList = new ArrayList<>();
                                Firebase pointsRef = User.firebaseRef.child("points").child(upcomingEvent.sport).child(User.uid);
                                pointsRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if ( snapshot.getValue() != null)
                                            pointsList.add(Integer.parseInt(snapshot.getValue().toString()));
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                    }
                                });
                                try{
                                    Thread.sleep(200,1);
                                }
                                catch(InterruptedException exc){}
                                int points, eventPoints = 0;
                                if(pointsList.size()!=0)
                                    points  = pointsList.get(0);
                                else points = 0;

                                int locationErrors = 0;

                                while (locationErrors <= 3 && eventPoints <= 10)
                                {
                                    requestUpdates(mLocationListeners,mLocationManager);
                                    // Get location
                                    final ArrayList<Double> coordinates = new ArrayList<>();
                                    Firebase coordRef = User.firebaseRef.child("users").child(User.uid).child("location");
                                    coordRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            for (DataSnapshot data : snapshot.getChildren() )
                                            {
                                                if ( data.getKey().equalsIgnoreCase("latitude") )
                                                    coordinates.add(Double.parseDouble(data.getValue()+""));
                                                if ( data.getKey().equalsIgnoreCase("longitude") )
                                                    coordinates.add(Double.parseDouble(data.getValue() + ""));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                        }
                                    });
                                    try{
                                        Thread.sleep(100,1);
                                    }
                                    catch(InterruptedException exc){}
                                    if ( coordinates.size() >= 2)
                                    {
                                        User.firebaseRef.child("mesgEventsNrEvenimente").setValue("Coordonate noi" + coordinates.get(0) + " " + coordinates.get(1));

                                        // Get distance between current location and event location
                                        float[] distance = new float[10];
                                        Location.distanceBetween(coordinates.get(0), coordinates.get(1), upcomingEvent.latitude, upcomingEvent.longitude, distance);

                                        Log.v("distance1", coordinates.get(0)+ " " + coordinates.get(1) + " vs " + upcomingEvent.latitude + " " + upcomingEvent.longitude);
                                        Log.v("distance2",distance[0]+" ");

                                        if ( distance[0] >= 500 ) {
                                            locationErrors++;
                                        }
                                        // If user is in location ( difference < 500 m ) add points to the eventPoints
                                        else{
                                            eventPoints++;
                                        }
                                    }
                                    // Update points with the ones received for the current event and update in Database
                                    points += eventPoints;
                                    User.firebaseRef.child("points").child(upcomingEvent.sport).child(User.uid).setValue(points+"");
                                    try{
                                        Thread.sleep(100000,1);
                                    }
                                    catch(InterruptedException exc){}
                                }
                            }
                        }
                    }

                    try {
                        Thread.sleep(limitTime*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        Thread eventParticipationThread = new Thread(eventParticipationChecker);
        eventParticipationThread.start();

        Runnable eventChecker = new Runnable() {
            public void run() {
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
                               sendNotification(i, 30, ai.sport, ai.others, ai.place, ai.date);
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
        //Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    //Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
       //Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}



