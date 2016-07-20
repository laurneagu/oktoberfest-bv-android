package larc.ludicon.Activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


//import com.batch.android.Batch;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;


import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import larc.ludicon.Adapters.LeftPanelItemClicker;
import larc.ludicon.Adapters.LeftSidePanelAdapter;
import larc.ludicon.R;
import larc.ludicon.UserInfo.ActivityInfo;
import larc.ludicon.UserInfo.User;
import larc.ludicon.Services.FriendlyService;
import larc.ludicon.Utils.Event;
import larc.ludicon.Utils.MainPageUtils.ViewPagerAdapter;
import larc.ludicon.Utils.ui.SlidingTabLayout;

import android.support.v4.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog dialog;
    private int TIMEOUT = 80;

    private ViewFlipper flipper;
    private int currentPage = 0; // 0 = friends, 1 = my
    Button frButton;
    Button myButton;

    /* SlideTab */
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"My Activities","Around Me"};
    int Numboftabs =2;
    boolean addedSwipe = false;
    final ArrayList<String> favoriteSports = new ArrayList<>();
    int userRange = 100;
    private final ArrayList<Event> myEventsList = new ArrayList<>();

    public MainActivity(){
        try{
            broadcastManager = LocalBroadcastManager.getInstance(this);
            broadcastManager.registerReceiver(
                    receiveIsHappening, new IntentFilter("ServiceToMain_ReceiveIsHappening"));
            broadcastManager.registerReceiver(receiveStartResponse,
                    new IntentFilter("ServiceToMain_StartResponse"));
        }
        catch(Exception e){
            broadcastManager=null;
            e.printStackTrace();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void saveUnsavedPointstoDatabaseReference()
    {
        // Check if there are any unsaved points in SharedPref and put them on DatabaseReference
        Map<String,Integer> unsavedPointsMap = new HashMap<>();
        SharedPreferences pSharedPref = getSharedPreferences("Points", Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString("UnsavedPointsMap", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String key = keysItr.next();
                    Integer value = (Integer) jsonObject.get(key);
                    unsavedPointsMap.put(key, value);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        for( Map.Entry<String,Integer> entry : unsavedPointsMap.entrySet() )
        {
            // Get sport of the current event(entry)
            DatabaseReference sportNameRef = User.firebaseRef.child("events").child(entry.getKey().toString());
            Log.v("entry:",entry.getKey().toString() + " " + entry.getValue().toString());

            final int unsavedPoints = entry.getValue();
            final String eventID = entry.getKey();

            final ArrayList<String> eventSport = new ArrayList<>();
            sportNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    for( DataSnapshot data : snapshot.getChildren() )
                    {
                        Log.v("data",data.getKey().toString());
                        if ( data.getKey().toString().compareToIgnoreCase("sport") == 0)
                        {
                            eventSport.add(data.getValue().toString());
                            Log.v("Sport",data.getValue().toString());
                            getActualPoints(data.getValue().toString(),unsavedPoints,eventID);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });

        }
        // Clear UnsavedPointsMap from SharedPref
        pSharedPref.edit().remove("UnsavedPointsMap").commit();
    }

    private void getActualPoints(final String sport,final int unsavedPoints,final String eventID)
    {
        // Get and update total number of points for user in sport
        DatabaseReference pointsRef = User.firebaseRef.child("points").child(sport).child(User.uid);
        //final ArrayList<Integer> points = new ArrayList<>();
        pointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.getValue() != null)
                    writeToDatabaseReference(sport, Integer.parseInt(snapshot.getValue().toString()), unsavedPoints, eventID);
                else
                    writeToDatabaseReference(sport, 0, unsavedPoints, eventID);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

    private void writeToDatabaseReference(final String sport, int points, final int unsavedPoints, final String eventID)
    {
        DatabaseReference pointsRef = User.firebaseRef.child("points").child(sport).child(User.uid);

        pointsRef.setValue(points + unsavedPoints);

        // Update points for each event in user's details
        User.firebaseRef.child("users").child(User.uid).child("events").child(eventID).child("points").setValue(unsavedPoints);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Batch.onStart(this);
        Batch.User.getEditor()
                .setIdentifier(User.uid)
                .save(); // Don't forget to save the changes!
        */
        // getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        /* Slide Tab */
        //toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //setSupportActionBar(toolbar); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        saveUnsavedPointstoDatabaseReference();

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
        /**************/





        /*
        flipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        flipper.setInAnimation(this, R.anim.right_enter);
        flipper.setOutAnimation(this, R.anim.left_out);
         frButton = (Button)findViewById(R.id.fractbutton);
         myButton = (Button)findViewById(R.id.myactbutton);
        addFriendsActivityButtonEventListener();
        addMyActivityButtonEventListener();
        */

        final Locale locale = Locale.getDefault();

        dialog = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait", true);

        // Check if there are any unsaved points in SharedPref and put them on DatabaseReference
        Map<String, Integer> unsavedPointsMap = new HashMap<>();
        SharedPreferences pSharedPref = getSharedPreferences("Points", Context.MODE_PRIVATE);
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString("UnsavedPointsMap", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    Integer value = (Integer) jsonObject.get(key);
                    unsavedPointsMap.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, Integer> entry : unsavedPointsMap.entrySet()) {
            // Get sport of the current event(entry)
            DatabaseReference sportNameRef = User.firebaseRef.child("events").child(entry.getKey()).child("sport");
            final ArrayList<String> eventSport = new ArrayList<>();
            sportNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    eventSport.add(snapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });
            try {
                Thread.sleep(100, 1);
            } catch (Exception exc) {
            }

            // Get and update total number of points for user in sport
            DatabaseReference pointsRef = User.firebaseRef.child("points").child(eventSport.get(0)).child(User.uid);
            final ArrayList<Integer> points = new ArrayList<>();
            pointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    points.add(Integer.parseInt(snapshot.getValue().toString()));
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });
            try {
                Thread.sleep(100, 1);
            } catch (Exception exc) {
            }
            pointsRef.setValue(points.get(0) + entry.getValue());

            // Update points for each event in user's details
            User.firebaseRef.child("users").child("events").child(entry.getKey().toString()).child("points").setValue(entry.getValue());

        }
        // Clear UnsavedPointsMap from SharedPref
        pSharedPref.edit().remove("UnsavedPointsMap").commit();

        // Background Service:
        if (!isMyServiceRunning(FriendlyService.class)) {

            Intent mServiceIntent = new Intent(this, FriendlyService.class);
            //startService(mServiceIntent);
            bindService(mServiceIntent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {

                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            }, Context.BIND_AUTO_CREATE | Context.BIND_ABOVE_CLIENT);
            startService(mServiceIntent);
        }

        // Get user's favorite sports
        DatabaseReference usersRef = User.firebaseRef.child("users").child(User.uid);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSN : snapshot.getChildren()) {
                    if (dataSN.getKey().equalsIgnoreCase("sports"))
                        for (DataSnapshot data : dataSN.getChildren()) {
                            if (data.getKey() != null && data.getKey() != " ")
                                favoriteSports.add(data.getKey().toString());
                        }
                    if (dataSN.getKey().equalsIgnoreCase("range")) {
                        userRange = Integer.parseInt(dataSN.getValue().toString());
                    }
                }
                continueUpdatingTimeline();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });



    }
    private LocalBroadcastManager broadcastManager=null;



    double userLatitude = 0;
    double userLongitude = 0;
    public void continueUpdatingTimeline() {

        // Get user's last known location from SharedPref
        SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences("LocationPrefs", 0);
        userLatitude = Double.parseDouble(sharedPref.getString("curr_latitude", "0"));
        userLongitude = Double.parseDouble(sharedPref.getString("curr_longitude", "0"));

        //Clean up shared pref for events: just for debugging
        SharedPreferences.Editor editor = getSharedPreferences("UserDetails", 0).edit();
        String connectionsJSONString = new Gson().toJson(null);
        editor.putString("events", connectionsJSONString);
        editor.commit();
        // Update sharedpref for events:
        editor = getSharedPreferences("UserDetails", 0).edit();
        editor.putString("uid", User.uid);
        editor.commit();
        DatabaseReference usersRef = User.firebaseRef.child("users").child(User.uid).child("events");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final List<ActivityInfo> activityInfos = new ArrayList<ActivityInfo>();

                if (snapshot == null)
                    Log.v("NULL", "Snapshot e null");
                for (DataSnapshot data : snapshot.getChildren()) {
                    final String id = data.getKey().toString();
                    for( DataSnapshot child : data.getChildren() ) {
                        if ( child.getKey().compareToIgnoreCase("participation") == 0 && (Boolean) child.getValue() == true) {
                            DatabaseReference eventRef = User.firebaseRef.child("events").child(data.getKey().toString());
                            eventRef.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    ActivityInfo ai = new ActivityInfo();
                                    ai.id = id;
                                    boolean mustAddEventToList = true;
                                    for (DataSnapshot details : dataSnapshot.getChildren()) {
                                        if (details.getKey().toString().equalsIgnoreCase("users")) {
                                            int count = 0;

                                            for (DataSnapshot user : details.getChildren()) {
                                                count++;
                                            }
                                            ai.others = count;

                                        }
                                        if (details.getKey().toString().equalsIgnoreCase("active")) {
                                            mustAddEventToList = Boolean.parseBoolean(details.getValue().toString());
                                        }
                                        if (details.getKey().toString().equalsIgnoreCase("date")) {
                                            ai.date = new Date(details.getValue().toString());
                                        }
                                        if (details.getKey().toString().equalsIgnoreCase("sport")) {
                                            ai.sport = details.getValue().toString();
                                        }
                                        if (details.getKey().toString().equalsIgnoreCase("description")) {
                                            ai.description = details.getValue().toString();
                                        }
                                        if (details.getKey().toString().equalsIgnoreCase("roomCapacity")) {
                                            ai.roomCapacity = Integer.parseInt(details.getValue().toString());
                                        }
                                        if (details.getKey().toString().equalsIgnoreCase("place")) {
                                            for (DataSnapshot eventData : details.getChildren()) {
                                                if (eventData.getKey().toString().equalsIgnoreCase("latitude"))
                                                    ai.latitude = Double.parseDouble(eventData.getValue().toString());
                                                if (eventData.getKey().toString().equalsIgnoreCase("longitude"))
                                                    ai.longitude = Double.parseDouble(eventData.getValue().toString());
                                                if (eventData.getKey().toString().equalsIgnoreCase("name"))
                                                    ai.place = eventData.getValue().toString();
                                            }

                                        }
                                    }
                                    // If event's sport is not in user's favourites do not include it
                                    if ( !favoriteSports.contains(ai.sport) )
                                        mustAddEventToList = false;

                                    // Get distance between last known location and event location
                                    //float[] distance = new float[10];
                                    //Location.distanceBetween(userLatitude, userLongitude, ai.latitude, ai.longitude, distance);

                                    // If distance from user to event is greater than the selected range do not include it
                                   // if ( distance[0] > userRange * 1000 )
                                    //    mustAddEventToList = false;
                                    //

                                    String connectionsJSONString = getSharedPreferences("UserDetails", 0).getString("events", null);
                                    Type type = new TypeToken<List<ActivityInfo>>() {}.getType();
                                    List<ActivityInfo> events = null;
                                    if (connectionsJSONString != null) {
                                        events = new Gson().fromJson(connectionsJSONString, type);
                                    }
                                    if (events == null ) {
                                        events = new ArrayList<ActivityInfo>();
                                        if ( mustAddEventToList )
                                            events.add(ai);
                                    } else {
                                        Boolean exist = false;
                                        for (ActivityInfo act : events) {
                                            // Trash detection
                                            if (ai.date == null || act.date == null ) {
                                                exist = true;
                                                break;
                                            }
                                            if (ai.date.compareTo(act.date) == 0) {
                                                exist = true;
                                            }
                                        }
                                        if (!exist && mustAddEventToList ) {
                                            events.add(ai);
                                        }
                                    }

                                    //sort by date
                                    Collections.sort(events, new Comparator<ActivityInfo>() {
                                        @Override
                                        public int compare(ActivityInfo lhs, ActivityInfo rhs) {
                                            return lhs.date.compareTo(rhs.date);
                                        }
                                    });

                                    SharedPreferences.Editor editor = getSharedPreferences("UserDetails", 0).edit();
                                    connectionsJSONString = new Gson().toJson(events);
                                    editor.putString("events", connectionsJSONString);
                                    editor.commit();

                                }

                                @Override
                                public void onCancelled(DatabaseError firebaseError) {

                                }
                            });
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });

        // Left side panel
        mDrawerList = (ListView) findViewById(R.id.leftMenu);
        initializeLeftSidePanel();

        User.setImage();

        // User picture and name for HEADER MENU
        TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText(User.getFirstName(getApplicationContext()) + " " + User.getLastName(getApplicationContext()));

        ImageView userPic = (ImageView) findViewById(R.id.userPicture);
        Drawable d = new BitmapDrawable(getResources(), User.image);
        userPic.setImageDrawable(d);
        // -------------------------------------------------------------------------------------------------------------

    /*
        final SwipeRefreshLayout mSwipeRefreshLayout1 = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh1);
        mSwipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
                mSwipeRefreshLayout1.setRefreshing(false);
            }
        });
*/
        /*
        final SwipeRefreshLayout mSwipeRefreshLayout2 = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh2);
        mSwipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
                mSwipeRefreshLayout2.setRefreshing(false);
            }
        });
        */


        updateList();

    }

    public void hideHappeningRightNow(){
        RelativeLayout rlCurrEvent = (RelativeLayout)findViewById(R.id.currEventLayout);
        rlCurrEvent.setVisibility(View.GONE);
    }


    public void showHappeningNow(ActivityInfo ce){

        final ActivityInfo currentEvent = ce;

//        getSharedPreferences("UserDetails", 0).edit().putString("currentEventState","0").commit(); // pending

        RelativeLayout rlCurrEvent = (RelativeLayout)findViewById(R.id.currEventLayout);

        ViewGroup.LayoutParams params = rlCurrEvent.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        rlCurrEvent.setLayoutParams(params);

        // Fill the current event details

        final TextView firstPart = (TextView)findViewById(R.id.firstPartofTextCurrEvent);
        final TextView secondPart = (TextView) findViewById(R.id.secondPartofTextCurrEvent);
        final TextView time = (TextView) findViewById(R.id.timeTextCurrEvent);
        final TextView place = (TextView) findViewById(R.id.placeTextCurrEvent);
        final ImageView icon = (ImageView) findViewById(R.id.sportIconCurrEvent);
        final ImageButton share = (ImageButton) findViewById(R.id.sharefb_btnCurrEvent);
        final Button changeStateButton = (Button) findViewById(R.id.stateChangeButton);
        final Chronometer timer = (Chronometer) findViewById( R.id.chronometer );
        //final TextView descriprionText = (TextView) findViewById(R.id.);
        //final TextView players = (TextView) findViewById(R.id.players);


        // Set name and picture for the first user of the event

        String uri = "@drawable/" + currentEvent.sport.toLowerCase().replace(" ", "");

        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);

        icon.setImageDrawable(res);
        firstPart.setText("You are playing " + currentEvent.sport);
        String audience = "";
        if (currentEvent.others-1 > 1) {
            audience = " with " + (currentEvent.others-1) + " others";
        }
        else {
            audience = " with no others";
        }
        secondPart.setText(audience);

        if(currentEvent.place != null )
            place.setText(currentEvent.place);
        else
            place.setText("Unknown");

        time.setText("Now");





        // Start/Stop Button
        changeStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = getSharedPreferences("UserDetails", 0).getString("currentEventState", "0");

                if (Integer.parseInt(state) == 0) { // is start => Stop
                    Intent intent = new Intent("MainToService_StartRequest");
                    intent.putExtra("bla", "bla");
                    if(broadcastManager != null)
                        broadcastManager.sendBroadcast(intent);

                } else if (Integer.parseInt(state) == 1) { // is Stop => Hide
                    timer.stop();
                    changeStateButton.setVisibility(View.GONE);
                    getSharedPreferences("UserDetails", 0).edit().putString("currentEventState", "2").commit(); // stopped
                    Toast.makeText(getApplicationContext(), "Yaay! Activity finished in " + timer.getText().toString(), Toast.LENGTH_LONG).show();

                }

            }
        });

        // Share on facebook
        final ShareDialog shareDialog = new ShareDialog(this);
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
                        .setContentTitle(User.getFirstName(getApplicationContext()) + " is playing " + currentEvent.sport + audience + " at " + place)
                        .setContentDescription("Ludicon ! Let's go and play !")
                        .build();

                if (ShareDialog.canShow(ShareLinkContent.class) == true)
                    shareDialog.show(content);

            }
        });
    }

    // Start response from service:
    private BroadcastReceiver receiveStartResponse = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            String msg = intent.getStringExtra("Response");
            final Button changeStateButton = (Button) findViewById(R.id.stateChangeButton);
            final Chronometer timer = (Chronometer) findViewById( R.id.chronometer );

            if(msg == "0"){ // Ok, start, location ok
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();
                changeStateButton.setText("Stop");
                changeStateButton.setBackgroundColor(Color.parseColor("#BF3636"));
                Toast.makeText(getApplicationContext(), "Activity started. Do not close the application if you want to sweat on points.", Toast.LENGTH_LONG).show();
            }
            else{ // location is not the right one
                Toast.makeText(getApplicationContext(), "You are not in the right location!", Toast.LENGTH_LONG).show();
            }


        }
    };

    private BroadcastReceiver receiveIsHappening = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            String msg = intent.getStringExtra("isActive");


            if(msg == "0"){ // New event right Now, show Happening now
                Toast.makeText(getApplicationContext(), "Awesome, you have an activity right now! :-)", Toast.LENGTH_LONG).show();
                /// Get Current event:
                Gson gson = new Gson();
                String json = getSharedPreferences("UserDetails", 0).getString("currentEvent", "");
                final ActivityInfo currentEvent = gson.fromJson(json, ActivityInfo.class);

                if ( currentEvent != null ) {
                    showHappeningNow(currentEvent);
                    updateList();
                }
            }
            else{ // Event ended
                hideHappeningRightNow();
                updateList();
            }


        }
    };

    public void updateList()
    {

        // Envent lists:
        DatabaseReference userRef = User.firebaseRef.child("events"); // check events
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                myEventsList.clear();

                final ArrayList<Event> eventList = new ArrayList<>();

                final ArrayList<Event> friendsEventsList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Event event = new Event();
                    boolean isPublic = true;
                    boolean doIParticipate = false;
                    boolean mustAddEventToList = true;
                    event.id = data.getKey();
                    Map<String, Boolean> participants = new HashMap<String, Boolean>();


                    for (DataSnapshot details : data.getChildren()) {

                        if (details.getKey().toString().equalsIgnoreCase("active"))
                            mustAddEventToList = Boolean.parseBoolean(details.getValue().toString());

                        if (details.getKey().toString().equalsIgnoreCase("creatorName"))
                            event.creatorName = details.getValue().toString();

                        if (details.getKey().toString().equalsIgnoreCase("creatorImage"))
                            event.profileImageURL = details.getValue().toString();

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

                        if (details.getKey().toString().equalsIgnoreCase("roomCapacity")) {
                            event.roomCapacity = Integer.parseInt(details.getValue().toString());
                        }

                        if (details.getKey().toString().equalsIgnoreCase("date"))
                            event.date = new Date(details.getValue().toString());

                        if (details.getKey().toString().equalsIgnoreCase("place")) {
                            Map<String, Object> position = (Map<String, Object>) details.getValue();
                            double latitude = (double) position.get("latitude");
                            double longitude = (double) position.get("longitude");
                            String addressName = (String) position.get("name");
                            event.place = addressName;
                            event.latitude = latitude;
                            event.longitude = longitude;
                        }

                        if (details.getKey().toString().equalsIgnoreCase("users")) {
                            for (DataSnapshot user : details.getChildren()) {
                                String userID = user.getKey().toString();
                                if (userID.equalsIgnoreCase(User.uid)) {
                                    doIParticipate = true;
                                    participants.put(user.getKey().toString(), (Boolean) user.getValue());
                                    //break;
                                } else {
                                    participants.put(user.getKey().toString(), (Boolean) user.getValue());
                                }
                            }
                        }
                    }

                    //TODO - copy

                    // If event's sport is not in user's favourites do not include it
                    if (!favoriteSports.contains(event.sport))
                        mustAddEventToList = false;

                    // Get distance between last known location and event location
                    // float[] distance = new float[10];
                    //Location.distanceBetween(userLatitude, userLongitude, event.latitude, event.longitude, distance);

                    // If distance from user to event is greater than the selected range do not include it
                    //  if ( distance[0] > userRange * 1000 )
                    //     mustAddEventToList = false;

                    // Insert event in the correct list
                    //if (new Date().before(event.date) && isPublic) {
                    if ((new Date().getTime() < event.date.getTime()) && isPublic && mustAddEventToList) {

                        event.usersUID = participants;

                        if (doIParticipate) {
                            myEventsList.add(event);
                        } else {
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
                SharedPreferences.Editor editor = getSharedPreferences("UserDetails", 0).edit();
                editor.putString("myEvents", new Gson().toJson(myEventsList));
                editor.commit();

                /* Friends */
                TimelineAroundActAdapter fradapter = new TimelineAroundActAdapter(friendsEventsList, getApplicationContext());
                ListView frlistView = (ListView) findViewById(R.id.events_listView1);
                if (frlistView != null) {
                    frlistView.setAdapter(fradapter);
                    // frlistView.setScrollingCacheEnabled(true);
                }

                /* My */
                TimelineMyActAdapter myadapter = new TimelineMyActAdapter(myEventsList, getApplicationContext());
                ListView mylistView = (ListView) findViewById(R.id.events_listView2);
                if (mylistView != null) {
                    mylistView.setAdapter(myadapter);
                    // mylistView.setScrollingCacheEnabled(true);
                }

                /*Swipe */
                if (!addedSwipe) {
                    final SwipeRefreshLayout mSwipeRefreshLayout1 = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh1);
                    mSwipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            updateList();
                            mSwipeRefreshLayout1.setRefreshing(false);
                        }
                    });
                    addedSwipe = true;
                }


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
                timer.schedule(delayedThreadStartTask, TIMEOUT * 2 * eventList.size());

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

    public void addFriendsActivityButtonEventListener(){

        frButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage != 0){
                    currentPage = 0;
                    myButton.setBackgroundColor(Color.parseColor("#237bbe"));
                    frButton.setBackgroundColor(Color.parseColor("#0e64a6"));
                    flipper.setInAnimation(getApplicationContext(), R.anim.right_enter);
                    flipper.setOutAnimation(getApplicationContext(), R.anim.left_out);
                    flipper.showNext();
                }
            }
        });
    }

    public void addMyActivityButtonEventListener(){
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage != 1) {
                    currentPage = 1;
                    frButton.setBackgroundColor(Color.parseColor("#237bbe"));
                    myButton.setBackgroundColor(Color.parseColor("#0e64a6"));
                    flipper.setInAnimation(getApplicationContext(), R.anim.left_enter);
                    flipper.setOutAnimation(getApplicationContext(), R.anim.right_out);
                    flipper.showPrevious();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Left side panel
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Left side menu
    public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, MainActivity.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), MainActivity.this);

        final ImageButton showPanel = (ImageButton) findViewById(R.id.showPanel);
        showPanel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        // Toggle efect on left side panel
        mDrawerToggle = new android.support.v4.app.ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    // Adapter for the Around activities tab
    public class TimelineAroundActAdapter extends BaseAdapter implements ListAdapter {

         class ViewHolder {
            TextView name;
            ImageView profilePicture;
            TextView firstPart;
             TextView secondPart;
             TextView time;
             TextView place;
             ImageView icon;
             ImageButton details;
             ImageButton join;
             TextView players;
             TextView description;
        };
        private ArrayList<Event> list = new ArrayList<>();
        private Context context;
        final ListView listView = (ListView) findViewById(R.id.events_listView1);

        public TimelineAroundActAdapter(ArrayList<Event> list, Context context) {
            this.list = list;
            this.context = context;



        }

        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }
        @Override
        public long getItemId(int pos) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder = null;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.timeline_list_layout, null);

                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.nameLabel);
                holder.profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
                holder.firstPart = (TextView) view.findViewById(R.id.firstPartofText);
                holder.secondPart = (TextView) view.findViewById(R.id.secondPartofText);
                holder.time = (TextView) view.findViewById(R.id.timeText);
                holder.place = (TextView) view.findViewById(R.id.placeText);
                holder.icon = (ImageView) view.findViewById(R.id.sportIcon);
                holder.details = (ImageButton) view.findViewById(R.id.details_btn);
                holder.join = (ImageButton) view.findViewById(R.id.join_btn);
                holder.description = (TextView) view.findViewById(R.id.descriptionID);
                holder.players = (TextView) view.findViewById(R.id.playersID);



                view.setTag(holder);
            }
            else {
                holder = (ViewHolder)view.getTag();
            }

            /*
            final TextView name = (TextView) view.findViewById(R.id.nameLabel);
            final ImageView profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
            final TextView firstPart = (TextView) view.findViewById(R.id.firstPartofText);
            final TextView secondPart = (TextView) view.findViewById(R.id.secondPartofText);
            final TextView time = (TextView) view.findViewById(R.id.timeText);
            final TextView place = (TextView) view.findViewById(R.id.placeText);
            final ImageView icon = (ImageView) view.findViewById(R.id.sportIcon);
            final ImageButton details = (ImageButton) view.findViewById(R.id.details_btn);
            final ImageButton join = (ImageButton) view.findViewById(R.id.join_btn);
            */

            // Set name and picture for the first user of the event
            //final String userUID = list.get(position).getFirstUser();

            holder.name.setText(list.get(position).creatorName.split(" ")[0]);
            Picasso.with(context).load(list.get(position).profileImageURL).into( holder.profilePicture);


            // Redirect to user profile on picture click
            holder.profilePicture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("uid", list.get(position).getFirstUser());
                    startActivity(intent);
                }
            });

            /*
            DatabaseReference userRef = User.firebaseRef.child("users").child(userUID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for ( DataSnapshot data : snapshot.getChildren() ) {
                        if( (data.getKey()).compareTo("name") == 0) {
                            name.setText(data.getValue().toString());
                        }
                        if( (data.getKey()).compareTo("profileImageURL") == 0 )
                            new DownloadImageTask(profilePicture).execute(data.getValue().toString());
                    }
                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });*/

            String uri = "@drawable/" + list.get(position).sport.toLowerCase().replace(" ", "");

            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);

            holder.icon.setImageDrawable(res);
            holder.firstPart.setText("Will play " + list.get(position).sport);
            if ((list.get(position).usersUID.size() - 1) > 1) {
                holder.secondPart.setText(" with " + (list.get(position).usersUID.size() - 1) + " others");
            } else if ((list.get(position).usersUID.size() - 1) == 1) {
                holder.secondPart.setText(" with 1 other");
            } else {
                holder.secondPart.setText(" with no others");
            }

            holder.description.setText("\"" + list.get(position).description + "\"");
            holder.players.setText( list.get(position).usersUID.size() + "/" + list.get(position).roomCapacity);


            /*
            firstPart.setText("Will play " + list.get(position).sport);
            if ((list.get(position).usersUID.size() - 1) > 1) {
                secondPart.setText(" with " + (list.get(position).noUsers) + " others");
            } else if (list.get(position).noUsers  == 1) {
                secondPart.setText(" with 1 other");
            } else {
                secondPart.setText(" with no others");
            }*/

            if(list.get(position) != null )
                holder.place.setText(list.get(position).place);
            else
                holder.place.setText("Unknown");
            Calendar c = Calendar.getInstance();
            Date today = c.getTime();
            int todayDay = getDayOfMonth(today);
            int todayMonth = today.getMonth();
            int todayYear = today.getYear();

            String day;
            if ( todayDay == getDayOfMonth(list.get(position).date) && todayMonth == list.get(position).date.getMonth() && todayYear == list.get(position).date.getYear() )
                day = "Today";
            else if ( todayDay == ( getDayOfMonth(list.get(position).date) - 1 ) && todayMonth == list.get(position).date.getMonth() && todayYear == list.get(position).date.getYear() )
                day = "Tomorrow";
            else day = getDayOfMonth(list.get(position).date) + "/" + (list.get(position).date.getMonth()+1) + "/" + (list.get(position).date.getYear()+1900);
            String dateHour = list.get(position).date.getHours() + "";
            String dateMin = list.get(position).date.getMinutes()+ "";
            if(dateHour.equalsIgnoreCase("0")) dateHour += "0";
            if(dateMin.equalsIgnoreCase("0")) dateMin += "0";
            String hour = dateHour + ":" + dateMin;
            holder.time.setText(day + " at " + hour);
            holder.details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getApplicationContext(), "Hei, wait for it..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), EventDetails.class);
                    intent.putExtra("eventUid", list.get(position).id);
                    startActivity(intent);
                }
            });
            holder.join.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    DatabaseReference usersRef = User.firebaseRef.child("events").child(list.get(position).id).child("users");
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Map<String, Object> map = new HashMap<>();
                            for (DataSnapshot data : snapshot.getChildren()) {

                                map.put(data.getKey(), data.getValue());
                            }

                            map.put(User.uid,true);
                            // NOTE: I need userRef to set the map value
                            DatabaseReference userRef = User.firebaseRef.child("events").child(list.get(position).id).child("users");
                            userRef.updateChildren(map);


                            Map<String,Object> inEv = new HashMap<>();
                            inEv.put("participation",true);
                            inEv.put("points", 0);

                            Map<String,Object> ev =  new HashMap<String,Object>();
                            ev.put(list.get(position).id, inEv);
                            list.remove(position);
                            User.firebaseRef.child("users").child(User.uid).child("events").updateChildren(ev);
                            updateList();
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                        }
                    });
                }
            });

            /*
            try{
                Thread.sleep(50,1);
            }
            catch(InterruptedException exc ) {}
            */

            return view;
        }
    }


    public static int getDayOfMonth(Date aDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    // Adapter for the My pending activities tab
    public class TimelineMyActAdapter extends BaseAdapter implements ListAdapter {

        class ViewHolder{
             TextView name ;
             ImageView profilePicture;
             TextView firstPart;
             TextView secondPart;
             TextView time;
             TextView place;
             ImageView icon;
             ImageButton details;
            TextView description;
            TextView players;
        }

        private ArrayList<Event> list = new ArrayList<>();
        private Context context;
        final ListView listView = (ListView) findViewById(R.id.events_listView2);

        public TimelineMyActAdapter(ArrayList<Event> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }
        @Override
        public long getItemId(int pos) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = convertView;
            ViewHolder holder = null;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.timeline_list_myactivities_layout, null);

                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.nameLabel);
                holder.profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
                holder.firstPart = (TextView) view.findViewById(R.id.firstPartofText);
                holder.secondPart = (TextView) view.findViewById(R.id.secondPartofText);
                holder.time = (TextView) view.findViewById(R.id.timeText);
                holder.place = (TextView) view.findViewById(R.id.placeText);
                holder.icon = (ImageView) view.findViewById(R.id.sportIcon);
                holder.details = (ImageButton) view.findViewById(R.id.details_btn);
                holder.description = (TextView) view.findViewById(R.id.descriptionID);
                holder.players = (TextView) view.findViewById(R.id.playersID);

                view.setTag(holder);
            }

            else {
                holder = (ViewHolder)view.getTag();
            }
            /*
            final TextView name = (TextView) view.findViewById(R.id.nameLabel);
            final ImageView profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
            final TextView firstPart = (TextView) view.findViewById(R.id.firstPartofText);
            final TextView secondPart = (TextView) view.findViewById(R.id.secondPartofText);
            final TextView time = (TextView) view.findViewById(R.id.timeText);
            final TextView place = (TextView) view.findViewById(R.id.placeText);
            final ImageView icon = (ImageView) view.findViewById(R.id.sportIcon);
            final ImageButton details = (ImageButton) view.findViewById(R.id.details_btn);
            */

            // Set name and picture for the first user of the event
            //final String userUID = list.get(position).getFirstUser();
            String firstName = list.get(position).creatorName.split(" ")[0];
            holder.name.setText(firstName);
            Picasso.with(context).load(list.get(position).profileImageURL).into(holder.profilePicture);

            // Redirect to user profile on picture click
            holder.profilePicture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (User.uid.equals(list.get(position).creator)) {
                        Toast.makeText(context, "This is you ! We can't compare with yourself..", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("uid", list.get(position).getFirstUser());
                        startActivity(intent);
                    }
                }
            });

            /*
            DatabaseReference userRef = User.firebaseRef.child("users").child(userUID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for ( DataSnapshot data : snapshot.getChildren() ) {
                        if( (data.getKey()).compareTo("name") == 0) {
                            name.setText(data.getValue().toString());
                        }
                        if( (data.getKey()).compareTo("profileImageURL") == 0 )
                            new DownloadImageTask(profilePicture).execute(data.getValue().toString());
                    }
                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });*/

            String uri = "@drawable/" + list.get(position).sport.toLowerCase().replace(" ", "");

            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);

            holder.icon.setImageDrawable(res);
            holder.firstPart.setText("Will play " + list.get(position).sport);
            if ((list.get(position).usersUID.size() - 1) > 1) {
                holder.secondPart.setText(" with " + (list.get(position).usersUID.size() - 1) + " others");
            } else if ((list.get(position).usersUID.size() - 1) == 1) {
                holder.secondPart.setText(" with 1 other");
            } else {
                holder.secondPart.setText(" with no others");
            }
            /*
            firstPart.setText("Will play " + list.get(position).sport);
            if ((list.get(position).usersUID.size() - 1) > 1) {
                secondPart.setText(" with " + (list.get(position).noUsers) + " others");
            } else if (list.get(position).noUsers  == 1) {
                secondPart.setText(" with 1 other");
            } else {
                secondPart.setText(" with no others");
            }*/

            holder.description.setText("\"" + list.get(position).description + "\"");
            holder.players.setText( list.get(position).usersUID.size() + "/" + list.get(position).roomCapacity);

            if(list.get(position) != null ) {
                if(list.get(position).isOfficial==0){
                    holder.place.setTextColor(Color.DKGRAY);
                }
                holder.place.setText(list.get(position).place);
            }
            else
                holder.place.setText("Unknown");

            Calendar c = Calendar.getInstance();
            Date today = c.getTime();
            int todayDay = getDayOfMonth(today);
            int todayMonth = today.getMonth();
            int todayYear = today.getYear();

            String day;
            if ( todayDay == getDayOfMonth(list.get(position).date) && todayMonth == list.get(position).date.getMonth() && todayYear == list.get(position).date.getYear() )
                day = "Today";
            else if ( todayDay == ( getDayOfMonth(list.get(position).date) - 1 ) && todayMonth == list.get(position).date.getMonth() && todayYear == list.get(position).date.getYear() )
                day = "Tomorrow";
            else day = getDayOfMonth(list.get(position).date) + "/" + (list.get(position).date.getMonth()+1) + "/" + (list.get(position).date.getYear()+1900);
            String dateHour = list.get(position).date.getHours() + "";
            String dateMin = list.get(position).date.getMinutes()+ "";
            if(dateHour.equalsIgnoreCase("0")) dateHour += "0";
            if(dateMin.equalsIgnoreCase("0")) dateMin += "0";
            String hour = dateHour + ":" + dateMin;
            holder.time.setText(day + " at " + hour);
            /*details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Hei, wait for it..", Toast.LENGTH_SHORT).show();
                }
            });
            */

            holder.details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getApplicationContext(), "Hei, wait for it..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), EventDetails.class);
                    intent.putExtra("eventUid", list.get(position).id);
                    startActivity(intent);
                }
            });

            /*
            try{
                Thread.sleep(50,1);
            }
            catch(InterruptedException exc ) {}
            */

            return view;
        }
    }
}
