package larc.ludicon.Utils.Location;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import larc.ludicon.R;

/**
 * Created by Ciprian on 2/12/2016.
 */
public class ActivitiesLocationListener implements LocationListener {
    private GoogleMap m_map;
    private Application m_activity_context;

    public static boolean hasSetPosition = false;

    public ActivitiesLocationListener(Application activity_context){
        m_activity_context = activity_context;
    }
    @Override
    public void onLocationChanged(Location location) {
        GPS_Positioning.setCurrentLocation(location);

        if(m_map != null & !hasSetPosition){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            m_map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("You are here"));
            m_map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

            // Point the user only one time on the Map
            hasSetPosition= true;

            // Save also this values
            SharedPreferences sharedPref = m_activity_context.getSharedPreferences("LocationPrefs", 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("curr_latitude", String.valueOf(location.getLatitude()));
            editor.putString("curr_longitude", String.valueOf(location.getLongitude()));
            editor.commit();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public void BindMap(GoogleMap i_map){
        m_map = i_map;
    }
}