package larc.ludicon.Utils.Location_GPS;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ciprian on 2/12/2016.
 */
public class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        GPS_Positioning.setCurrentLocation(location);
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}