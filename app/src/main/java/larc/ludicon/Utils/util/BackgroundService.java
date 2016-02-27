package larc.ludicon.Utils.util;

import android.app.IntentService;
import android.content.Intent;

import larc.ludicon.UserInfo.User;

/**
 * Created by Andrei on 2/27/2016.
 */
public class BackgroundService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BackgroundService(String name) {
        super(name);
    }

    public BackgroundService() {
        super("BackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        int i = 0;
        while(true){
            User.firebaseRef.child("mesg").child(User.uid).child("backgroundService").setValue(i + " times");
            i++;
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(i == Integer.MAX_VALUE){
                i = 0;
            }
        }

    }
}
