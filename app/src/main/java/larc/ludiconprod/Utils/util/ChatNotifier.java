/*
package larc.ludiconprod.Utils.util;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import larc.ludiconprod.R;

public class ChatNotifier {

    public static int chatNotificationFirstIndex = 0;
    public static int chatNotificationIndex = 0;
    public static Object lock = new Object();
    public Object waitForPhoto = new Object();
    String chat;
    String photoUrl;

    public void sendNotification(Service m_service, Object m_systemService,Resources m_resources, int notificationNumber, String author, String message, String date, String chatUid, boolean isChatMsg){
        chat = chatUid;

        Runnable getPhoto = GetPhotoThread();
        Thread photoThread = new Thread(getPhoto);
        photoThread.start();

        NotificationManager manager = (NotificationManager)m_systemService;
        Notification myNotification;

        Intent intent = new Intent(m_service, IntroActivity.class);                         // here was "this"
        if(!isChatMsg)
            intent.putExtra("chatUID", chatUid);

        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(m_service, uniqueInt, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(m_service);

        builder.setAutoCancel(false);
        if (!isChatMsg)
            builder.setTicker("New Message from " + author + " in private chat" );
        else
            builder.setTicker("New Message from " + author + " in event chat" );


        builder.setContentTitle(author);
        if(message.contains("[%##")){
            builder.setContentText("You are invited at this event!!");
        }
        else {
            builder.setContentText(message);
        }
        builder.setSmallIcon(R.drawable.logo_notif);

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
        builder.setLights(Color.BLUE, 3000, 3000);


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

*/
