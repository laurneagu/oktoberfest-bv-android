package larc.oktoberfestprod.ChatNotification;

import android.content.SharedPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);
    }


    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("regId", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}