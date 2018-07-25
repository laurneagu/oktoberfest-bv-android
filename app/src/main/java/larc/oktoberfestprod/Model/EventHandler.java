/*
package larc.ludiconprod.Model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import larc.ludiconprod.Adapters.MainActivity.AroundMeAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.Gamification.HappeningNow;
import larc.ludiconprod.Layer.DataPersistence.EventPersistence;
import larc.ludiconprod.Layer.DataPersistence.LocationPersistence;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.Location.LocationInfo;
import larc.ludiconprod.Utils.util.DateManager;

*/
/**
 * Created by LaurUser on 7/5/2017.
 *//*


public class EventHandler {
    private int TIMEOUT = 80;

    // Singleton for connection to database
    private static EventHandler instance = null;
    protected EventHandler() {}
    public static EventHandler getInstance() {
        if(instance == null) {
            instance = new EventHandler();
        }
        return instance;
    }

    public void getEvents(final Activity activity,
                          final ArrayList<Event> myEventsList,
                          final ArrayList<String> favouriteSports,
                          final ProgressDialog dialog,
                          final MyAdapter myAdapter, final AroundMeAdapter aroundMeAdapter,
                          final int userRange,
                          final HappeningNow happeningNow
                          ){

        // Get user's last known location from persistence layer
        LocationPersistence locationPersistence = LocationPersistence.getInstance();
        LocationInfo userLocationInfo = locationPersistence.getUserLocation(activity);

        final LocationInfo userLocationInfoFinal = userLocationInfo;

        // Event lists
        DatabaseReference userRef = User.firebaseRef.child("events");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                myEventsList.clear();

                final ArrayList<Event> friendsEventsList = new ArrayList<>();

                // Get current event from persistence layer
                EventPersistence eventPersistence = EventPersistence.getInstance();
                final Event currentEvent = eventPersistence.getCurrentEvent(activity);
                String currentEventUID = "";
                if (currentEvent != null) currentEventUID = currentEvent.id;

                for (DataSnapshot data : snapshot.getChildren()) {
                    Event event = new Event();
                    boolean isPublic = true;
                    double distance = 0;
                    boolean doIParticipate = false;
                    boolean mustAddEventToList = true;
                    event.id = data.getKey();
                    Map<String, Boolean> participants = new HashMap<String, Boolean>();

                    for (DataSnapshot details : data.getChildren()) {
                        if (details.getKey().toString().equalsIgnoreCase("active"))
                            mustAddEventToList = Boolean.parseBoolean(details.getValue().toString());
                        if (details.getKey().toString().equalsIgnoreCase("creatorName"))
                            event.creatorName = details.getValue().toString();
                        if (details.getKey().toString().equalsIgnoreCase("users"))
                            event.noUsers = (int) details.getChildrenCount();
                        if (details.getKey().toString().equalsIgnoreCase("creatorImage"))
                            event.profileImageURL = details.getValue().toString();
                        if (details.getKey().toString().equalsIgnoreCase("active")) {
                            event.active = Boolean.parseBoolean(details.getValue().toString());
                            mustAddEventToList = event.active;
                        }
                        if (details.getKey().toString().equalsIgnoreCase("privacy"))
                            if (details.getValue().toString().equalsIgnoreCase("private"))
                                isPublic = false;
                        if (details.getKey().toString().equalsIgnoreCase("sport"))
                            event.sport = details.getValue().toString();
                        if (details.getKey().equalsIgnoreCase("createdBy"))
                            event.creator = details.getValue().toString();
                        if (details.getKey().toString().equalsIgnoreCase("isofficial"))
                            event.isOfficial = Integer.parseInt(details.getValue().toString());
                        if (details.getKey().toString().equalsIgnoreCase("description"))
                            event.description = details.getValue().toString();
                        if (details.getKey().toString().equalsIgnoreCase("roomCapacity"))
                            event.roomCapacity = Integer.parseInt(details.getValue().toString());
                        if (details.getKey().toString().equalsIgnoreCase("date"))
                            event.date = DateManager.convertFromSecondsToDate((long) details.getValue());
                        if (details.getKey().toString().equalsIgnoreCase("place")) {
                            Map<String, Object> position = (Map<String, Object>) details.getValue();
                            double latitude = (double) position.get("latitude");
                            double longitude = (double) position.get("longitude");

                            String addressName = (String) position.get("name");
                            event.place = addressName;
                            event.latitude = latitude;
                            event.longitude = longitude;

                            // it it is first time
                            if (userLocationInfoFinal.latitude != 0 && userLocationInfoFinal.longitude != 0) {
                                Location el = new Location("");
                                el.setLatitude(latitude);
                                el.setLongitude(longitude);

                                Location ml = new Location("");
                                ml.setLatitude(userLocationInfoFinal.latitude);
                                ml.setLongitude(userLocationInfoFinal.longitude);

                                distance = ml.distanceTo(el);
                            }
                        }

                        if (details.getKey().toString().equalsIgnoreCase("users")) {
                            for (DataSnapshot user : details.getChildren()) {
                                String userID = user.getKey().toString();
                                if (userID.equalsIgnoreCase(User.uid)) {
                                    doIParticipate = true;
                                    //TODO: change true with player is accepted or not
                                    participants.put(user.getKey().toString(), true);
                                } else {
                                    participants.put(user.getKey().toString(), true);
                                }
                            }
                        }
                    }

                    if (event.creator.equals(User.uid)) {
                        User.firebaseRef.child("events").child(event.id).child("creatorImage").setValue(User.profilePictureURL);
                        User.firebaseRef.child("events").child(event.id).child("creatorName").setValue(User.name);
                        event.profileImageURL = User.profilePictureURL;
                        event.creatorName = User.name;
                    }

                    // If event's sport is not in user's favourites do not include it
                    if (!favouriteSports.contains(event.sport))
                        mustAddEventToList = false;

                    if (event.noUsers == event.roomCapacity)
                        mustAddEventToList = false;

                    if (event.active && doIParticipate && (new Date().getTime() < event.date.getTime())) {
                        if (currentEventUID != event.id) myEventsList.add(event);
                    } else if ((new Date().getTime() < event.date.getTime()) && isPublic && mustAddEventToList) {
                        if (distance < (double) userRange * 1000) {
                            System.out.println("Distance to event id : " + data.getKey() +  "is: " + distance + ", user range is set to:" + userRange);
                            event.usersUID = participants;
                            friendsEventsList.add(event);
                        }
                    }
                }

                // Sort by date
                Collections.sort(friendsEventsList, new Comparator<Event>() {
                    @Override
                    public int compare(Event lhs, Event rhs) {
                        return lhs.date.compareTo(rhs.date);
                    }
                });
                // Sort by date
                Collections.sort(myEventsList, new Comparator<Event>() {
                    @Override
                    public int compare(Event lhs, Event rhs) {
                        return lhs.date.compareTo(rhs.date);
                    }
                });

                // Save my events also locally - to be used in the Create Activity
                eventPersistence.setMyEvents(activity, myEventsList);

                // Update adapter entries
                aroundMeAdapter.setListOfEvents(friendsEventsList);
                myAdapter.setListOfEvents(myEventsList);

                // Check happening now
                System.out.println("Events were gathered from database");

                // TEST force to start now
                //myEventsList.get(0).date = new Date();
                //myEventsList.get(0).latitude = userLocationInfoFinal.latitude;
                //myEventsList.get(0).longitude = userLocationInfoFinal.longitude;

                happeningNow.checkHappeningNow(myEventsList);

                // Dismiss loading dialog after  2 * TIMEOUT * eventList.size() ms
                Timer timer = new Timer();
                TimerTask delayedThreadStartTask = new TimerTask() {
                    @Override
                    public void run() {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        }).start();
                    }
                };

                timer.schedule(delayedThreadStartTask, TIMEOUT * 6);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }
}

*/
