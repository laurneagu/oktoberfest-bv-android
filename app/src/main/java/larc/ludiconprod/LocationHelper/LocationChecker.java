/*
package larc.ludiconprod.LocationHelper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;

import larc.ludiconprod.Layer.DataPersistence.EventPersistence;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.Location.GPSTracker;

*/
/**
 * Created by LaurUser on 7/5/2017.
 *//*


public class LocationChecker {

    private Context context;
    public int maxDistanceMeters = 1500;

    // Singleton
    private static LocationChecker instance = null;
    protected LocationChecker() {
    }
    public static LocationChecker getInstance() {
        if(instance == null) {
            instance = new LocationChecker();
        }
        return instance;
    }

    public void setContext(Context newContext){
        if(instance != null)
            instance.context = newContext;
    }

    public void testLocationIsActivated() {
        boolean isGPSEnabled = false;

        final Context context = instance.context;

        try {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            if (lm != null) {
                isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }

            if (!isGPSEnabled) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MyAlertDialogStyle));
                builder.setMessage("Location services are not enabled")
                        .setPositiveButton("Activate location services", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(myIntent);
                            }
                        })
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isLocationOk(Activity activity, DistanceValue dist) {
        dist.value = -1.0;

        GPSTracker gps = new GPSTracker(activity.getApplicationContext(), (FragmentActivity)activity);
        if (!gps.canGetLocation()) return false;

        Location current = gps.getLocation();
        gps.stopUsingGPS();
        if (current != null) {
            EventPersistence eventPersistence = EventPersistence.getInstance();
            Event currentEvent = eventPersistence.getCurrentEvent(activity);

            if (currentEvent != null) {
                Location targetLocation = new Location("");
                targetLocation.setLatitude(currentEvent.latitude);
                targetLocation.setLongitude(currentEvent.longitude);
                double distance = current.distanceTo(targetLocation);

                dist.value = distance;
                if (distance <= maxDistanceMeters) {
                    // TODO custom maxDistance by Event/Event Location
                    return true;
                } else {
                    return false;
                }
            }
        } else return false;
        return true;
    }
}
*/
