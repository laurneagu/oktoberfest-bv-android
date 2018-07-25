package larc.oktoberfestprod.Layer.DataPersistence;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by LaurUser on 7/7/2017.
 */

public class ChatPersistence {

    private final String userDetailsString = "UserDetails";
    private final String chatNotificationStatus = "ChatNotificationStatus";

    // Singleton
    private static ChatPersistence instance = null;
    protected ChatPersistence() {}

    public static ChatPersistence getInstance() {
        if(instance == null) {
            instance = new ChatPersistence();
        }
        return instance;
    }

    public String getChatNotificationStatus(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userDetailsString, 0);

        String currentEventState = sharedPreferences.getString(chatNotificationStatus, "0");

        return currentEventState;
    }

    public void setChatNotificationStatus(Activity activity, String newState) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userDetailsString, 0);
        sharedPreferences.edit().putString(chatNotificationStatus, newState).commit();
    }
}
