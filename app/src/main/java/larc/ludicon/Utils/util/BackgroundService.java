package larc.ludicon.Utils.util;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;


import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import larc.ludicon.Activities.MainActivity;
import larc.ludicon.R;
import larc.ludicon.UserInfo.ActivityInfo;
import larc.ludicon.UserInfo.User;

/**
 * Created by Andrei on 2/27/2016.
 */
public class BackgroundService extends Service {

    public static final long MIN = 60*1000;

    void sendNotification(int notificationNumber, int minutesToEvent, String sport, int otherPlayers , String place, Date date){
        NotificationManager manager;
        Notification myNotification;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent("com.rj.notitfications.SECACTIVITY");
        PendingIntent pendingIntent = PendingIntent.getActivity(BackgroundService.this, 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(BackgroundService.this);

        builder.setAutoCancel(false);
        builder.setTicker("You have one activity in " + minutesToEvent + " minutes");

        builder.setContentTitle(sport + " activity remainder");
        builder.setContentText("In less than " + minutesToEvent + " minutes you will play " + sport.toLowerCase() + " with " + otherPlayers + " other player" + (otherPlayers != 1 ? "s" : "") + " at " + place);
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setSubText("Have fun!");   //API level 16
        //builder.setNumber(number * 100);
        builder.build();

        myNotification = builder.getNotification();
        manager.notify(notificationNumber, myNotification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


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
                           if(ai.date.after(now) && ai.date.before(limit) && !checkNotSent30.containsKey(ai.date)){
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
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}



