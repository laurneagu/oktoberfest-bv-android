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
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Objects;

import larc.ludicon.Activities.ChatListActivity;
import larc.ludicon.Activities.IntroActivity;
import larc.ludicon.R;
import larc.ludicon.UserInfo.User;

/**
 * Created by Andrei on 4/18/2016.
 */
public class ChatNotifier {

    public static int chatNotificationFirstIndex = 0;
    public static int chatNotificationIndex = 0;
    public static Object lock = new Object();
    public Object waitForPhoto = new Object();
    String chat;
    String photoUrl;

    public void sendNotification(Service m_service, Object m_systemService,Resources m_resources, int notificationNumber, String author, String message, String date, String chatUid){
        chat = chatUid;

        Runnable getPhoto = GetPhotoThread();
        Thread photoThread = new Thread(getPhoto);
        photoThread.start();

        NotificationManager manager = (NotificationManager)m_systemService;
        Notification myNotification;

        Intent intent = new Intent(m_service, IntroActivity.class);                         // here was "this"
        PendingIntent pendingIntent = PendingIntent.getActivity(m_service, 1, intent, 0);   // here was "FriendlyService.this"
        Notification.Builder builder = new Notification.Builder(m_service);

        builder.setAutoCancel(false);
        builder.setTicker("New Message");

        builder.setContentTitle(author);
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.logo_notif);
        builder.setColor(Color.parseColor("#0e3956"));

        // Asta e Daca vreau sa vad poza celuilalt
        // asta implica sa adaug url-ul in firebase la event!!!!!!!!!!!
//        synchronized (waitForPhoto){
//            try {
//                waitForPhoto.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }


        Bitmap largeIcon = BitmapFactory.decodeResource(m_resources, R.drawable.logo);
        builder.setLargeIcon(largeIcon);

        builder.setContentIntent(pendingIntent);
        //builder.setOngoing(true);
        builder.setSubText(date);   //API level 16
        //builder.setNumber(number * 100);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(soundUri);
        builder.build();

       // builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        builder.setLights(Color.RED, 3000, 3000);


        myNotification = builder.getNotification();
        myNotification.defaults |= Notification.DEFAULT_VIBRATE;
        manager.notify(notificationNumber, myNotification);
    }

    public void deleteNotification(Object m_systemService, int notificationNumber){
        NotificationManager manager = (NotificationManager)m_systemService;
        manager.cancel(notificationNumber);
    }

    public Runnable GetPhotoThread() {
        return new Runnable() {
            public void run() {

                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("chat").child(chat).child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if(!data.getKey().equalsIgnoreCase(User.uid)){
                                photoUrl="";//TODO
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            }

        };
    }
}
