package larc.oktoberfestprod.Utils.Location;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by LaurUser on 4/10/2016.
 */

public class ServiceLocationListener implements android.location.LocationListener{

    private static final String TAG = "SERVICELL";
    private static final int LOCATION_INTERVAL = 540000; // 9 minutes
    private static final float LOCATION_DISTANCE = 150; // 150 meters

    private double m_latitude;
    private double m_longitude;
    private static Context context;
    public ServiceLocationListener(Context passedContext)
    {
        context =  passedContext;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.e(TAG, "Location changed: " + location.getLatitude() + " - " + location.getLongitude());

        // Put latitude and longitute in SharedPref
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        m_latitude = location.getLatitude();
        m_longitude = location.getLongitude();

        // Writing data to SharedPreferences
        editor.putString("latitude",m_latitude + "");
        editor.putString("longitude",m_longitude + "");
        editor.commit();
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

    public void requestUpdates(final LocationManager mLocationManager) {
        final LocationListener locLis = this;

        WorkingWithThreads wwt = new WorkingWithThreads(mLocationManager,locLis);
        Thread tzero = new Thread(wwt);
        tzero.start();

    }
}
 class WorkingWithThreads implements Runnable {
     private static final String TAG = "SERVICELL";

     private static final int LOCATION_INTERVAL = 540000; // 9 minutes
     private static final float LOCATION_DISTANCE = 150; // 150 meters

     private LocationManager mLocationManager;
     private LocationListener mLocationListener;

     public Handler mHandler;

    public WorkingWithThreads(LocationManager locationManager, LocationListener locLis) {
        mLocationManager = locationManager;
        mLocationListener =locLis;
    }

     @Override
     public void run() {
         Looper.prepare();

         mHandler = new Handler() {
             public void handleMessage(Message msg) {

                 try {
                     mLocationManager.requestLocationUpdates(
                             LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                             mLocationListener);
                     mLocationManager.requestLocationUpdates(
                             LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                             mLocationListener);

                 } catch (java.lang.SecurityException ex) {
                     Log.i(TAG, "fail to request location update, ignore", ex);
                 } catch (IllegalArgumentException ex) {
                     Log.d(TAG, "network provider does not exist, " + ex.getMessage());
                 }
             }};

         Looper.loop();
     }
 }
