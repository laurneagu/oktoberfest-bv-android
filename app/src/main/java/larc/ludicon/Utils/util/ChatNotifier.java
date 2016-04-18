package larc.ludicon.Utils.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Date;

import larc.ludicon.Activities.IntroActivity;
import larc.ludicon.R;

/**
 * Created by Andrei on 4/18/2016.
 */
public class ChatNotifier {

    public void sendNotification(Service m_service, Object m_systemService,Resources m_resources, int notificationNumber, String author, String message, Date date){

        NotificationManager manager = (NotificationManager)m_systemService;
        Notification myNotification;

        Intent intent = new Intent(m_service, IntroActivity.class);                         // here was "this"
        PendingIntent pendingIntent = PendingIntent.getActivity(m_service, 1, intent, 0);   // here was "FriendlyService.this"
        Notification.Builder builder = new Notification.Builder(m_service);

        builder.setAutoCancel(false);
        builder.setTicker("New Message");

        builder.setContentTitle(author + ": " + message);
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.logo);

        Bitmap largeIcon = BitmapFactory.decodeResource(m_resources, R.drawable.logo);
        builder.setLargeIcon(largeIcon);

        builder.setContentIntent(pendingIntent);
        //builder.setOngoing(true);
        builder.setSubText(date.toString());   //API level 16
        //builder.setNumber(number * 100);
        builder.build();

        myNotification = builder.getNotification();
        manager.notify(notificationNumber, myNotification);
    }
}
