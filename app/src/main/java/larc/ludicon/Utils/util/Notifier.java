package larc.ludicon.Utils.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import java.util.Date;

import larc.ludicon.Activities.IntroActivity;
import larc.ludicon.R;

/**
 * Created by LaurUser on 4/10/2016.
 */
public class Notifier {

    public int getSportImg(String name){
        if(name.equalsIgnoreCase("basketball")){
           return R.drawable.basketball;
        }
        if(name.equalsIgnoreCase("cycling")){
            return R.drawable.cycling;
        }
        if(name.equalsIgnoreCase("football")){
            return R.drawable.football;
        }
        if(name.equalsIgnoreCase("jogging")){
            return R.drawable.jogging;
        }
        if(name.equalsIgnoreCase("squash")){
            return R.drawable.squash;
        }
        if(name.equalsIgnoreCase("tennis")){
            return R.drawable.tennis;
        }
        if(name.equalsIgnoreCase("pingpong")){
            return R.drawable.pingpong;
        }
        if(name.equalsIgnoreCase("volley")){
            return R.drawable.volley;
        }
        return R.drawable.logo;
    }

    public void sendNotification(Service m_service, Object m_systemService,Resources m_resources, int notificationNumber, int minutesToEvent, String sport, int otherPlayers , String place, Date date){

        NotificationManager manager = (NotificationManager)m_systemService;
        Notification myNotification;

        Intent intent = new Intent(m_service, IntroActivity.class);                         // here was "this"
        PendingIntent pendingIntent = PendingIntent.getActivity(m_service, 1, intent, 0);   // here was "FriendlyService.this"
        Notification.Builder builder = new Notification.Builder(m_service);

        builder.setAutoCancel(false);
        builder.setTicker("You have one activity in " + minutesToEvent + " minutes");

        builder.setContentTitle(sport + " activity remainder");
        builder.setContentText("In less than " + minutesToEvent + " minutes you will play " + sport.toLowerCase() + " with " + otherPlayers + " other player" + (otherPlayers != 1 ? "s" : "") + " at " + place);
        builder.setSmallIcon(R.drawable.logo_notif);
        builder.setColor(Color.parseColor("#0e3956"));

        int sportImg = getSportImg(sport.toLowerCase());

        Bitmap largeIcon = BitmapFactory.decodeResource(m_resources, sportImg);
        builder.setLargeIcon(largeIcon);

        builder.setContentIntent(pendingIntent);
        //builder.setOngoing(true);
        builder.setSubText("Have fun!");   //API level 16
        //builder.setNumber(number * 100);
        builder.build();

        myNotification = builder.getNotification();
        manager.notify(notificationNumber, myNotification);
    }
}
