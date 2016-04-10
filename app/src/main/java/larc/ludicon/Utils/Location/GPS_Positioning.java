package larc.ludicon.Utils.Location;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Ciprian on 2/12/2016.
 */
public class GPS_Positioning {

    private static Location currentLocation;

    public static void setCurrentLocation(Location location)
    {
        currentLocation = location;
    }
    public static Location getCurrentLocation()
    {
        return currentLocation;
    }
    public static LatLng getLatLng()
    {
        return new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
    }

}
