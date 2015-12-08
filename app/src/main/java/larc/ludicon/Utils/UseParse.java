package larc.ludicon.Utils;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

/**
 * Created by Ciprian on 12/5/2015.
 */
public class UseParse extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "7ynxx7uuHFrR4b5tEDDv3yEOPIFhcjsdSIUfDGxh", "sVYUFfdDYLmuqhxU9pxSVvdxRioC3jurlNJb41cw");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        /* for "saveEventually thread had an error." */
        ParseUser.enableAutomaticUser();
    }
}