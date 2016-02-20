package larc.ludicon.Utils;

//import com.parse.Parse;
//import com.parse.ParseInstallation;
//import com.parse.ParseUser;

import com.batch.android.Batch;
import com.batch.android.Config;

/**
 * Created by Ciprian on 12/5/2015.
 */

// TODO - Delete this class

public class UseParse extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        /* Batch Init */
        Batch.Push.setGCMSenderId("458732166636");
        Batch.setConfig(new Config("56C87CCE024DC2AEDE02625BFEABED"));

//        Parse.initialize(this, "7ynxx7uuHFrR4b5tEDDv3yEOPIFhcjsdSIUfDGxh", "sVYUFfdDYLmuqhxU9pxSVvdxRioC3jurlNJb41cw");
//        ParseInstallation.getCurrentInstallation().saveInBackground();
//
//        /* for "saveEventually thread had an error." */
//        ParseUser.enableAutomaticUser();
    }
}