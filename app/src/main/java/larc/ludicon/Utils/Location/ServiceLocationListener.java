package larc.ludicon.Utils.Location;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

import larc.ludicon.UserInfo.User;

/**
 * Created by LaurUser on 4/10/2016.
 */

public class ServiceLocationListener implements android.location.LocationListener{

    private static final String TAG = "SERVICELL";
    private static final int LOCATION_INTERVAL = 60000;
    private static final float LOCATION_DISTANCE = 10f;

    private double m_latitude;
    private double m_longitude;

    @Override
    public void onLocationChanged(Location location)
    {
        Log.e(TAG, "Location changed: " + location.getLatitude() + " - " + location.getLongitude());

        // LAUR - To be changed in shared preferences
        /*
        HashMap<String,String> map = new HashMap<>();
        map.put("latitude",location.getLatitude()+"");
        map.put("longitude", location.getLongitude() + "");
        User.firebaseRef.child("users").child(User.uid).child("location").setValue(map);
        */
        m_latitude = location.getLatitude();
        m_longitude = location.getLongitude();
    }

    public double getLatitude(){
        return m_latitude;
    }

    public double getLongitude(){
        return m_longitude;
    }

    @Override
    public void onProviderDisabled(String provider)
    {}
    @Override
    public void onProviderEnabled(String provider)
    {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {}

    public void requestUpdates(LocationManager mLocationManager)
    {
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    this);
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    this);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
    }
}
