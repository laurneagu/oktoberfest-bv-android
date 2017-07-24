/*
package larc.ludiconprod.Gamification;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import larc.ludiconprod.Activities.MainActivityVechi;
import larc.ludiconprod.Layer.DataPersistence.EventPersistence;
import larc.ludiconprod.Layer.DataPersistence.PointsPersistence;
import larc.ludiconprod.LocationHelper.DistanceValue;
import larc.ludiconprod.LocationHelper.LocationChecker;
import larc.ludiconprod.Model.Leaderboards;
import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.Location.GPSTracker;

*/
/**
 * Created by LaurUser on 7/6/2017.
 *//*


public class HappeningNow {
    // Singleton
    private static HappeningNow instance = null;
    protected HappeningNow() {
    }
    public static HappeningNow getInstance() {
        if(instance == null) {
            instance = new HappeningNow();
        }
        return instance;
    }

    private View view;
    private Activity activity;
    private MainActivityVechi fragmentActivity;
    private Resources resources;
    private LocationChecker locationChecker;
    public final Handler handlerChecker = new Handler();

    // The time between checks of location and points offering
    public long MIN = 60000; // should be 60000 for one minute
    public long INTERVAL = 5; // min

    public long TIMEOUT_EVENT = 120; // min

    public static void setData(View view, Activity activity,
                               MainActivityVechi fragmentActivity, Resources resources, LocationChecker locationChecker){
        if(instance == null) {
            instance = new HappeningNow();
        }
        instance.view = view;
        instance.resources = resources;
        instance.activity = activity;
        instance.fragmentActivity = fragmentActivity;
        instance.locationChecker = locationChecker;
    }

    public void checkHappeningNow(List<Event> events) {
        EventPersistence eventPersistence = EventPersistence.getInstance();

        System.out.println("Entered check happening now");

        // Check if there are any events
        if (events != null && events.size() != 0) {
            Date now = new Date();

            // Find the current pending event
            boolean found = false;
            int j = 0;

            while (j < events.size()) {
                if (events.get(j).date != null) {
                    if (now.after(events.get(j).date))
                        j++;
                    else {
                        found = true; break;
                    }
                }
            }
            Event upcomingEvent, lastEvent;

            if (!found) {
                upcomingEvent = null;
                lastEvent = events.get(events.size() - 1);
            } else {
                upcomingEvent = events.get(j);
                if (j > 0) lastEvent = events.get(j - 1);
                else lastEvent = null;
            }

            Date upEventDate;
            long diffMilis = 0;
            if (upcomingEvent != null) {
                upEventDate = upcomingEvent.date;
                now = new Date();
                diffMilis = Math.abs(upEventDate.getTime() - now.getTime()) + 3000;
            }

            if (lastEvent != null) {
                now = new Date();
                Date lastEventDate = lastEvent.date;

                long diffInMillisec = Math.abs(now.getTime() - lastEventDate.getTime());
                long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMillisec);
                double hours = (double) diffInSec / (double) 3600;

                if (hours < 1.0) {
                    // 0 - didn't start, 1 - started, 2 - stopped, 3 - nothing
                    String state = eventPersistence.getCurrentEventState(activity);
                    System.out.println("Event scheduled to happen now, state is " + state);

                    if (state.equalsIgnoreCase("0")) { // last event is the current one
                        eventPersistence.persistCurrentEvent(activity, lastEvent);

                        showHappeningNow(lastEvent);
                        if(fragmentActivity.isAdded()) {
                            fragmentActivity.updateListOfEvents(true);
                        }
                        eventPersistence.setCurrentEventState(activity, "0");

                    } else {
                        if (state.equalsIgnoreCase("1")) { // it is already started
                            showHappeningNow(lastEvent);
                            if(fragmentActivity.isAdded()) {
                                fragmentActivity.updateListOfEvents(true);
                            }
                        } else {
                            if (upcomingEvent != null && state.equalsIgnoreCase("2")) {
                                eventPersistence.persistCurrentEvent(activity, upcomingEvent);
                                delayHappeningNow(upcomingEvent, diffMilis, eventPersistence);
                            }
                        }
                    }

                } else {
                    if (upcomingEvent != null) {
                        eventPersistence.persistCurrentEvent(this.activity, upcomingEvent);
                        delayHappeningNow(upcomingEvent, diffMilis, eventPersistence);
                    }
                }

            } else {
                if (upcomingEvent != null) {
                    eventPersistence.persistCurrentEvent(activity, upcomingEvent);
                    delayHappeningNow(upcomingEvent, diffMilis, eventPersistence);
                }
            }
        }
    }

    public void delayHappeningNow(final Event ev, long milis, final EventPersistence eventPersistence) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showHappeningNow(ev);
                //fragmentActivity.updateListOfEvents(false);
                //eventPersistence.setCurrentEventState(activity, "0");
            }
        }, milis);
    }

    public void showHappeningNow(final Event currentEvent) {

        RelativeLayout rlCurrEvent = (RelativeLayout) this.view.findViewById(R.id.currEventLayout);

        ViewGroup.LayoutParams params = rlCurrEvent.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        rlCurrEvent.setLayoutParams(params);

        // Fill the current event details
        final TextView firstPart = (TextView) this.view.findViewById(R.id.firstPartofTextCurrEvent);
        final TextView secondPart = (TextView) this.view.findViewById(R.id.secondPartofTextCurrEvent);
        final TextView time = (TextView) this.view.findViewById(R.id.timeTextCurrEvent);
        final TextView place = (TextView) this.view.findViewById(R.id.placeTextCurrEvent);
        final ImageView icon = (ImageView) this.view.findViewById(R.id.sportIconCurrEvent);
        final ImageButton share = (ImageButton) this.view.findViewById(R.id.sharefb_btnCurrEvent);
        final Button changeStateButton = (Button) this.view.findViewById(R.id.stateChangeButton);
        final Chronometer timer = (Chronometer) this.view.findViewById(R.id.chronometer);

        // Set name and picture for the first user of the event
        String uri = "@drawable/" + currentEvent.sport.toLowerCase().replace(" ", "");

        int imageResource = this.resources.getIdentifier(uri, null, this.activity.getPackageName());
        Drawable res = this.resources.getDrawable(imageResource);

        icon.setImageDrawable(res);
        if (currentEvent.sport.equalsIgnoreCase("jogging"))
            firstPart.setText("You are " + currentEvent.sport);
        else
            firstPart.setText("You are playing " + currentEvent.sport);
        String audience = "";
        if (currentEvent.others - 1 > 1) {
            audience = " with " + (currentEvent.others - 1) + " others";
        } else {
            audience = " with no others";
        }
        secondPart.setText(audience);

        if (currentEvent.place != null)
            place.setText(currentEvent.place);
        else
            place.setText("Unknown");

        time.setText("Now");

        changeStateButton.setTag(0);
        // Start/Stop Button
        changeStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((int) changeStateButton.getTag() == 0) { // is start => Stop

                    if (!GPSTracker.canGetGPSLocation(activity.getApplicationContext())) {
                        Toast.makeText(activity.getApplicationContext(), "Activate gps location first!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    DistanceValue dist = new  DistanceValue();
                    if (locationChecker.isLocationOk(activity, dist)) { // Ok, start, location ok
                        timer.setBase(SystemClock.elapsedRealtime());
                        timer.start();

                        EventPersistence eventPersistence = EventPersistence.getInstance();
                        String state = eventPersistence.getCurrentEventState(activity);

                        if (!state.equalsIgnoreCase("1")) { // if it is not started yet
                            eventPersistence.setCurrentEventDateStarted(activity, new Date());
                            Toast.makeText(activity.getApplicationContext(),
                                    "Activity started. Do not close the application if you want to sweat on points.", Toast.LENGTH_LONG).show();
                            runPointsChecker();// Start points checker

                        }
                        changeStateButton.setText("Stop");
                        changeStateButton.setBackgroundColor(Color.parseColor("#BF3636"));
                        changeStateButton.setTag(1);

                        // Change state of event to running
                        eventPersistence.setCurrentEventState(activity, "1");

                    } else {
                        // Location is not right
                        Toast.makeText(activity.getApplicationContext(),
                                    "You are not in the right location!\n" + dist.value + " meters far away!", Toast.LENGTH_LONG).show();
                    }

                } else if ((int) changeStateButton.getTag() == 1) { // is Stop => Hide

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    // Get number of points gathered
                                    PointsPersistence pointsPersistence = PointsPersistence.getInstance();
                                    int currentP = pointsPersistence.getPoints(activity);

                                    // Change state of event to stopped
                                    EventPersistence eventPersistence = EventPersistence.getInstance();
                                    eventPersistence.setCurrentEventState(activity, "2");

                                    // Stop timer and hide buttons
                                    timer.stop();
                                    changeStateButton.setVisibility(View.GONE);
                                    Toast.makeText(activity.getApplicationContext(),
                                            "Yaay! Activity finished in " + timer.getText().toString() + "!\nYou got " + currentP + " points! :-)", Toast.LENGTH_LONG).show();
                                    hideHappeningRightNow();

                                    handlerChecker.removeCallbacks(rCheck);

                                    savePointsInDatabase();
                                    changeStateButton.setTag(2);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyAlertDialogStyle);
                    builder.setMessage("Are you sure? Once you press Stop you can't start again this event!").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                }
            }
        });

        EventPersistence eventPersistence = EventPersistence.getInstance();
        String alreadyStartedString = eventPersistence.getCurrentEventState(activity);
        int alreadyStarted = Integer.parseInt(alreadyStartedString);

        if (alreadyStarted == 1) {
            changeStateButton.performClick();

            String lastTimeString = eventPersistence.getCurrentEventDateStarted(activity);

            if (!lastTimeString.equalsIgnoreCase("0")) {
                Date startedTime = new Date(lastTimeString);
                Date now = new Date();
                long diffMilis = Math.abs(now.getTime() - startedTime.getTime());
                timer.setBase(SystemClock.elapsedRealtime() - diffMilis);
            }
        } else {
            Toast.makeText(activity.getApplicationContext(),
                    "Awesome, you have an activity right now! :)", Toast.LENGTH_LONG).show();
        }

        if(fragmentActivity.isAdded()) {
            // Share on facebook
            final ShareDialog shareDialog = new ShareDialog(fragmentActivity);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String audience = "";
                    if (currentEvent.others > 1)
                        audience = " with " + (currentEvent.others) + " others";
                    else
                        audience = " with no others";

                    String place = "";
                    if (currentEvent.place != null)
                        place = currentEvent.place;
                    else
                        place = "Unknown";

                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("http://ludicon.info/"))
                            .setImageUrl(Uri.parse("http://www.ludicon.info/img/sports/" + currentEvent.sport + ".png"))
                            .setContentTitle(User.getFirstName(activity.getApplicationContext()) + " is playing " + currentEvent.sport + audience + " at " + place)
                            .setContentDescription("Ludicon ! Let's go and play !")
                            .build();

                    if (ShareDialog.canShow(ShareLinkContent.class) == true)
                        shareDialog.show(content);

                }
            });
        }

        rlCurrEvent.setVisibility(View.VISIBLE);
    }

    public void stopHappeningNowAtTimeout() {
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Chronometer timer = (Chronometer) activity.findViewById(R.id.chronometer);

                PointsPersistence pointsPersistence = PointsPersistence.getInstance();
                int currentPoints = pointsPersistence.getPoints(activity);

                EventPersistence eventPersistence = EventPersistence.getInstance();
                eventPersistence.setCurrentEventState(activity, "2");
                Toast.makeText(activity.getApplicationContext(),
                        "Yaay! Activity finished in " + timer.getText().toString() + "!\nYou got " + currentPoints + " points! :-)", Toast.LENGTH_LONG).show();

                timer.stop();
                hideHappeningRightNow();

                handlerChecker.removeCallbacks(rCheck);
                savePointsInDatabase();
            }
        }, MIN * TIMEOUT_EVENT);
    }


    public void hideHappeningRightNow() {
        RelativeLayout rlCurrEvent = (RelativeLayout) activity.findViewById(R.id.currEventLayout);
        rlCurrEvent.setVisibility(View.GONE);
    }

    void savePointsInDatabase() {
        EventPersistence eventPersistence = EventPersistence.getInstance();
        PointsPersistence pointsPersistence = PointsPersistence.getInstance();
        Event currentEvent = eventPersistence.getCurrentEvent(activity);

        // Set points in database
        Leaderboards leaderboards = Leaderboards.getInstance();
        leaderboards.setPointsInDatabase(pointsPersistence.getPoints(activity), currentEvent.id);

        pointsPersistence.resetPoints(activity);
    }

    public void runPointsChecker() {
        handlerChecker.postDelayed(rCheck, INTERVAL * MIN);
        stopHappeningNowAtTimeout();
    }

    final Runnable rCheck = new Runnable() {
        public void run() {
            if (!GPSTracker.canGetGPSLocation(activity.getApplicationContext())) {
                Toast.makeText(activity.getApplicationContext(),
                        "Activate gps location first!", Toast.LENGTH_LONG).show();
                return;
            }
            DistanceValue dist = new DistanceValue();
            if (locationChecker.isLocationOk(activity, dist)) {
                EventPersistence eventPersistence = EventPersistence.getInstance();
                PointsPersistence pointsPersistence = PointsPersistence.getInstance();

                RewardLayer rewardLayer = RewardLayer.getInstance();
                int numberOfPoints = rewardLayer.rewardPointsByEventPriority(eventPersistence.getCurrentEvent(activity).priority);
                pointsPersistence.persistPoints(activity, numberOfPoints);
            } else {
                Toast.makeText(activity.getApplicationContext(),
                        "You are not in the right location!\nYou are " + Math.round(dist.value) + " meters away!", Toast.LENGTH_LONG).show();
            }
            handlerChecker.postDelayed(this, INTERVAL * MIN);
        }
    };

    */
/*
    public void onDestroyPage(){
        // Stop the event
        EventPersistence eventPersistence = EventPersistence.getInstance();
        eventPersistence.setCurrentEventState(activity, "2");
        handlerChecker.removeCallbacks(rCheck);

        // Save points
        savePointsInDatabase();
    }
    *//*

}
*/
