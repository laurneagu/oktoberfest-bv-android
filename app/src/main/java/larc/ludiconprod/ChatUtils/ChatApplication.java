package larc.ludiconprod.ChatUtils;

import com.google.firebase.database.DatabaseReference;

/**
 * @author Jenny Tong (mimming)
 * @since 12/5/14
 *
 * Initialize DatabaseReference with the application context. This must happen before the client is used.
 */
public class ChatApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //DatabaseReference.setAndroidContext(this);
    }
}
