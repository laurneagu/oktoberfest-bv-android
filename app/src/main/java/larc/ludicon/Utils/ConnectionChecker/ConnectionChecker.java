package larc.ludicon.Utils.ConnectionChecker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Ciprian on 11/18/2015.
 */
public class ConnectionChecker implements IConnectionChecker {
    @Override
    public boolean isNetworkAvailable(Context i_context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) i_context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
