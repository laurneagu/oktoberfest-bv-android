package larc.ludiconprod.Layer.DataPersistence;

import android.app.Activity;
import android.content.SharedPreferences;

import larc.ludiconprod.Utils.Location.LocationInfo;

/**
 * Created by LaurUser on 7/5/2017.
 */

public class LocationPersistence {
    final static String userDetailsString = "UserDetails";
    final static String currentLatitude = "current_latitude";
    final static String currentLongitude = "current_longitude";

    // Singleton
    private static LocationPersistence instance = null;
    protected LocationPersistence() {
    }
    public static LocationPersistence getInstance() {
        if(instance == null) {
            instance = new LocationPersistence();
        }
        return instance;
    }

    public void persistLocation(Activity activity, LocationInfo locationInfo) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(userDetailsString, 0).edit();

        editor.putString(currentLatitude, String.valueOf(locationInfo.latitude));
        editor.putString(currentLongitude, String.valueOf(locationInfo.longitude));
        editor.commit();
    }

    public LocationInfo getUserLocation(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userDetailsString, 0);

        String latitude = sharedPreferences.getString(currentLatitude, "0");
        String longitude = sharedPreferences.getString(currentLongitude, "0");

        double latitudeDouble = Double.parseDouble(latitude);
        double longitudeDouble = Double.parseDouble(longitude);

        return new LocationInfo(latitudeDouble, longitudeDouble);
    }
}
