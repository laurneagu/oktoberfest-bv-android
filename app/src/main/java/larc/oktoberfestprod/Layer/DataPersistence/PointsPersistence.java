package larc.oktoberfestprod.Layer.DataPersistence;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by LaurUser on 7/5/2017.
 */

public class PointsPersistence {
    final static String userDetailsString = "UserDetails";
    final static String currentEventPoints = "currentEventPointsCounter";

    final static String pointsString = "Points";
    final static String unsavedPointsString = "UnsavedPointsMap";

    // Singleton
    private static PointsPersistence instance = null;
    protected PointsPersistence() {
    }
    public static PointsPersistence getInstance() {
        if(instance == null) {
            instance = new PointsPersistence();
        }
        return instance;
    }

    public void persistPoints(Activity activity, int points) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userDetailsString, 0);

        String current = sharedPreferences.getString(currentEventPoints, "0");

        int currentValueInteger = Integer.parseInt(current);
        currentValueInteger += points;
        String newValueString = String.valueOf(currentValueInteger);

        sharedPreferences.edit().putString(currentEventPoints, newValueString).commit();
    }

    public int getPoints(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userDetailsString, 0);

        String currentPoints = sharedPreferences.getString(currentEventPoints, "0");
        int currentPointsInt = Integer.parseInt(currentPoints);

        return currentPointsInt;
    }

    public void resetPoints(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userDetailsString, 0);
        sharedPreferences.edit().putString(currentEventPoints, "0").commit();
    }

    public Map<String,Integer> getUnsavedPoints(Activity activity) throws JSONException {
        Map<String,Integer> unsavedPointsMap = new HashMap<>();
        SharedPreferences pointsSharedPreferences = activity.getSharedPreferences(pointsString, Context.MODE_PRIVATE);

        if (pointsSharedPreferences != null) {
            String jsonString = pointsSharedPreferences.getString(unsavedPointsString, (new JSONObject()).toString());
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> keysItr = jsonObject.keys();

            while (keysItr.hasNext()) {
                String key = keysItr.next();
                Integer value = (Integer) jsonObject.get(key);
                unsavedPointsMap.put(key, value);
            }
        }

        return unsavedPointsMap;
    }

    public void resetUnsavedPoints(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(pointsString, 0);
        sharedPreferences.edit().remove(unsavedPointsString).commit();
    }
}
