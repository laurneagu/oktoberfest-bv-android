package larc.ludiconprod.Utils.Tests;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import larc.ludiconprod.Activities.MainActivity;
import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;

public class BackgroundService2 extends Service {
    public static final String EXTRA_PLAYLIST="EXTRA_PLAYLIST";
    public static final String EXTRA_SHUFFLE="EXTRA_SHUFFLE";
    private boolean isPlaying=false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        play();
        return(START_NOT_STICKY);
    }

    @Override
    public void onDestroy() {
        stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return(null);
    }

    private void play() {
        int i = 0;
        while(true){
            User.firebaseRef.child("mesg").child(User.uid).child("backgroundService2").setValue(i + " times");
            i++;
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(i == Integer.MAX_VALUE){
                i = 0;
            }

            Notification note=new Notification(R.drawable.logo,
                    "Can you hear the music?",
                    System.currentTimeMillis());
            Intent ii =new Intent(this, MainActivity.class);

            ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);


            
            note.flags|=Notification.FLAG_NO_CLEAR;
            startForeground(1337, note);
        }
    }

    private void stop() {


        stopForeground(true);

    }
}