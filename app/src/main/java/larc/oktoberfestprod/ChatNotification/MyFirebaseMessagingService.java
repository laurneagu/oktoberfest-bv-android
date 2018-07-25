package larc.oktoberfestprod.ChatNotification;


import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

import larc.oktoberfestprod.Activities.IntroActivity;
import larc.oktoberfestprod.Activities.Notification;
import larc.oktoberfestprod.BottomBarHelper.BottomBarTab;
import larc.oktoberfestprod.Controller.Persistance;
import larc.oktoberfestprod.R;

import static larc.oktoberfestprod.Activities.Main.bottomBar;

/**
 * Created by ancuta on 8/30/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {



        if(remoteMessage.getData().get("type") != null && remoteMessage.getData().get("type").equalsIgnoreCase("chat")) {
            ArrayList<String> numberOfChatUnseen = Persistance.getInstance().getUnseenChats(getApplicationContext());
            Boolean chatIsAlreadyUnseen = false;
            for (int i = 0; i < numberOfChatUnseen.size(); i++) {
                if (remoteMessage.getData().get("chat").equalsIgnoreCase(numberOfChatUnseen.get(i))) {
                    chatIsAlreadyUnseen = true;
                    break;
                }
            }
            if (!chatIsAlreadyUnseen) {
                numberOfChatUnseen.add(remoteMessage.getData().get("chat"));
            }

            Persistance.getInstance().setUnseenChats(getApplicationContext(), numberOfChatUnseen);
        }

        if(!isAppRunning(getApplicationContext())){
            sendNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("body"),remoteMessage.getData().get("chat"));
        }
    }



    public boolean isAppRunning(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(context.getPackageName().toString())) {
            isActivityFound = true;
        }
        return isActivityFound;

    }

    private void sendNotification(String messageTitle,String messageBody,String chatId) {
        Intent intent = new Intent(this, IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 /* request code */, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        long[] pattern = {500,500,500,500,500};

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String displayMessage="";
        final String splitMessage[]=messageBody.split(" ");
        for(int i=0;i < splitMessage.length;i++) {
            if (splitMessage[i].length() > 21 && splitMessage[i].substring(0, 10).equalsIgnoreCase("$#@$@#$%^$") && splitMessage[i].substring(splitMessage[i].length() - 10).equalsIgnoreCase("$#@$@#$%^$")) {
                displayMessage+="this";
            }else{
                displayMessage+=splitMessage[i];
            }
            if(i < splitMessage.length) {
                displayMessage+=" ";
            }
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_notif)
                .setContentTitle(messageTitle)
                .setContentText(displayMessage)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setLights(Color.BLUE,1,1)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(chatId.hashCode() /* ID of notification */, notificationBuilder.build());
    }

}