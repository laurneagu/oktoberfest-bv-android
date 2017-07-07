package larc.ludiconprod.Activities;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import larc.ludiconprod.Adapters.MainActivity.AroundMeAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.Gamification.HappeningNow;
import larc.ludiconprod.Layer.DataPersistence.EventPersistence;
import larc.ludiconprod.Layer.DataPersistence.LocationPersistence;
import larc.ludiconprod.Layer.DataPersistence.PointsPersistence;
import larc.ludiconprod.Layer.DataPersistence.UserPersistence;
import larc.ludiconprod.LocationHelper.LocationChecker;
import larc.ludiconprod.Model.EventHandler;
import larc.ludiconprod.R;
import larc.ludiconprod.Service.FriendlyService;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.Location.GPSTracker;
import larc.ludiconprod.Utils.Location.LocationInfo;
import larc.ludiconprod.Utils.MainPageUtils.ViewPagerAdapter;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;

public class MainActivity extends Fragment {
    private ProgressDialog dialog;

    private View v;

    /* SlideTab */
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"My Activities", "Around Me"};
    int Numboftabs = 2;
    boolean addedSwipe = false;

    final ArrayList<String> favoriteSports = new ArrayList<>();
    private final ArrayList<Event> myEventsList = new ArrayList<>();
    int userRange = 100;

    private AroundMeAdapter fradapter;
    private MyAdapter myadapter;

    private HappeningNow happeningNow;
    private LocationChecker locationChecker;

    // Local Layer of data persistence
    private EventPersistence eventPersistence;
    private PointsPersistence pointsPersistence;
    private LocationPersistence locationPersistence;
    private UserPersistence userPersistence;

    private MainActivity currentFragment;
    private LocalBroadcastManager broadcastManager = null;

    private Context mContext;

    private LocationInfo userLocationInfo;

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {}
        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };

    public MainActivity() {
        try {
            broadcastManager = LocalBroadcastManager.getInstance(mContext);
            broadcastManager.registerReceiver(
                    receiveIsHappening, new IntentFilter("ServiceToMain_ReceiveIsHappening"));
            broadcastManager.registerReceiver(receiveStartResponse,
                    new IntentFilter("ServiceToMain_StartResponse"));

            currentFragment = this;

            locationChecker = LocationChecker.getInstance();
        } catch (Exception e) {
            broadcastManager = null;
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fradapter != null) fradapter.notifyDataSetChanged();
        if (myadapter != null) myadapter.notifyDataSetChanged();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) v.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // Method used for Crashlytics
    private void logUser() {
        Crashlytics.setUserIdentifier(User.uid);
        Crashlytics.setUserEmail(User.email);
        Crashlytics.setUserName(User.name);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext=inflater.getContext();
        v=inflater.inflate(R.layout.activity_main, container,false);

        if(locationChecker == null) locationChecker = LocationChecker.getInstance();
        locationChecker.setContext(mContext);

        try {
            super.onCreate(savedInstanceState);

            // Hide App bar
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
            if (Build.VERSION.SDK_INT < 16) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            String chatUID = getActivity().getIntent().getStringExtra("chatUID");
            if (chatUID != null) {
                Intent goToChatList = new Intent(getActivity(), ChatListActivity.class);
                goToChatList.putExtra("chatUID", chatUID);
                startActivity(goToChatList);
            }

            // Creating ViewPager Adapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs
            adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), Titles, Numboftabs);

            // Assigning ViewPager View and setting the adapter
            pager = (ViewPager) v.findViewById(R.id.pager);
            pager.setAdapter(adapter);

            // Assiging the Sliding Tab Layout View
            tabs = (SlidingTabLayout) v.findViewById(R.id.tabs);
            tabs.setDistributeEvenly(false); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(pager);

            // Initialize Crashlytics (Fabric)
            Fabric.with(getActivity(), new Crashlytics());
            logUser();

            dialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait", true);

            // Check if there are any unsaved points and save them
            pointsPersistence = PointsPersistence.getInstance();
            Map<String,Integer> unsavedPointsMap = pointsPersistence.getUnsavedPoints(getActivity());
            for (final Map.Entry<String, Integer> entry : unsavedPointsMap.entrySet()) {
                if(entry.getValue() == 0) continue;

                User.firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // Synchronize multi-threaded access
                        synchronized (User.firebaseRef)
                        {
                            // Update points for each event in user's details
                            User.firebaseRef.child("users").child("events").child(entry.getKey().toString()).child("points").setValue(entry.getValue());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) { }
                });
            }

            // Clear unsaved points from persistence layer
            pointsPersistence.resetUnsavedPoints(getActivity());

            // Background service
            if (!isMyServiceRunning(FriendlyService.class)) {
                Intent mServiceIntent = new Intent(getActivity(), FriendlyService.class);
                getActivity().bindService(mServiceIntent, mServiceConn, Context.BIND_AUTO_CREATE | Context.BIND_ABOVE_CLIENT);
                getActivity().startService(mServiceIntent);
            }

            // Get user's favourite sports and range
            DatabaseReference usersRef = User.firebaseRef.child("users").child(User.uid);
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot dataSN : snapshot.getChildren()) {
                        // Favourite sports
                        if (dataSN.getKey().equalsIgnoreCase("sports")){
                            favoriteSports.clear();
                            for (DataSnapshot data : dataSN.getChildren()) {
                                if (data.getKey() != null
                                     && data.getKey().toString().compareTo(" ") != 0
                                     && data.getKey().toString().compareTo("") != 0
                                     && data.getKey().toString().compareTo("openedApp") != 0)
                                     favoriteSports.add(data.getKey().toString());
                            }
                        }
                        // Range
                        if (dataSN.getKey().equalsIgnoreCase("range")) {
                            userRange = Integer.parseInt(dataSN.getValue().toString());
                        }
                    }
                    User.favouriteSports = favoriteSports;

                    System.out.println("User range is: " + userRange);
                    System.out.println("User favourite sports are: " + favoriteSports);

                    startUpdateTimeline();
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });
          } catch (Exception exc) { exc.printStackTrace();}
        return v;
    }

    public void startUpdateTimeline() {
        try {
            // First, test if location services are activated
            if (locationChecker == null){
                locationChecker = LocationChecker.getInstance();
            }
            locationChecker.testLocationIsActivated();

            // Initialize happening now
            happeningNow = HappeningNow.getInstance();
            if(this.isAdded()) {
                happeningNow.setData(v, getActivity(), this, getResources(), locationChecker);
            }

            // Get user's last known location from persistence layer
            locationPersistence = LocationPersistence.getInstance();
            userLocationInfo = locationPersistence.getUserLocation(getActivity());

            // If it is the first entry
            if (userLocationInfo.latitude <= 0 || userLocationInfo.longitude <= 0) {
                System.out.println("User location is: Latitude: " + userLocationInfo.latitude + " , Longitude: " + userLocationInfo.longitude);

                // Get location
                GPSTracker gps = new GPSTracker(getActivity().getApplicationContext(),  getActivity());
                if (gps.canGetLocation()) {
                    userLocationInfo.latitude = gps.getLatitude();
                    userLocationInfo.longitude = gps.getLongitude();
                    locationPersistence.persistLocation(getActivity(), userLocationInfo);

                    gps.stopUsingGPS();
                }

                System.out.println("User new location is: Latitude: " + userLocationInfo.latitude + " , Longitude: " + userLocationInfo.longitude);
            }

            // Clean up events saved
            eventPersistence = EventPersistence.getInstance();
            eventPersistence.cleanUpEvents(getActivity());

            // Update userID
            userPersistence = UserPersistence.getInstance();
            userPersistence.persistUserID(getActivity(), User.uid);

            updateListOfEvents(false);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public void updateListOfEvents(final boolean eventHappeningNow) {
        // stop swiping on my events
        final SwipeRefreshLayout mSwipeRefreshLayout2 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh2);
        mSwipeRefreshLayout2.setEnabled(false);
        mSwipeRefreshLayout2.setFocusable(false);

        /* Friends */
        fradapter = new AroundMeAdapter(new ArrayList<Event>(), getActivity().getApplicationContext(), getActivity(), getResources(), currentFragment);
        ListView frlistView = (ListView) v.findViewById(R.id.events_listView1);
        final FloatingActionButton cloudoflistview1 = (FloatingActionButton) v.findViewById(R.id.floatingButton1);
        cloudoflistview1.setVisibility(View.VISIBLE);

        frlistView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                Point size = new Point();
                display.getSize(size);
                int height = size.y;
                float density=metrics.ydpi;
                float bottombarpixels=250*(density/160);
                float value1= (float)((height-bottombarpixels)*1.5);
                float value2= (float)((height-bottombarpixels));
                if(visibleItemCount<totalItemCount) {
                    //cloudoflistview2.animate().translationY(20).setDuration(300);
                    if (lastItem == visibleItemCount) {
                        //cloudoflistview2.setVisibility(View.INVISIBLE);
                        //cloudoflistview1.animate().y(value1).setDuration(300);
                    } else{
                        //cloudoflistview1.animate().y(value2).setDuration(300);
                    }
                }
                else{
                    //cloudoflistview1.animate().translationY(20).setDuration(300);
                }
            }
        });
        cloudoflistview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userSports = User.firebaseRef.child("users").child(User.uid).child("sports");
                userSports.addListenerForSingleValueEvent(new ValueEventListener() { // get user sports
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // Get user's favourite sports
                        ArrayList<String> favouriteSports = new ArrayList<>();
                        for (DataSnapshot sport : snapshot.getChildren()) {
                            if(!favouriteSports.contains(sport.getKey().toString()))
                                favouriteSports.add(sport.getKey().toString());
                        }
                        Intent createNewIntent = new Intent(getActivity(), CreateNewActivity.class);
                        createNewIntent.putStringArrayListExtra("favourite_sports", favouriteSports);
                        getActivity().startActivity(createNewIntent);
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                    }
                });
            }
        });

        if (frlistView != null) {
            if (/*friendsEventsList.size() == 0 && */!eventHappeningNow) {
                // place holder no events created
            }
            else frlistView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            frlistView.setAdapter(fradapter);
        }

        /* My */
        myadapter = new MyAdapter(new ArrayList<Event>(), getActivity().getApplicationContext(), getActivity(), getResources());
        ListView mylistView = (ListView) v.findViewById(R.id.events_listView2);
        final FloatingActionButton cloudoflistview2 = (FloatingActionButton) v.findViewById(R.id.floatingButton2);
        cloudoflistview2.setVisibility(View.VISIBLE);

        mylistView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                Point size = new Point();
                display.getSize(size);
                int height = size.y;
                float density=metrics.ydpi;
                float bottombarpixels=250*(density/160);
                float value1= (float)((height-bottombarpixels)*1.5);
                float value2= (float)((height-bottombarpixels));
                //cloudoflistview2.animate().translationY(20).setDuration(300);
                if(visibleItemCount<totalItemCount) {
                    //cloudoflistview2.animate().translationY(20).setDuration(300);
                    if (lastItem == totalItemCount) {
                        //cloudoflistview2.setVisibility(View.INVISIBLE);
                        //cloudoflistview2.animate().y(value1).setDuration(300);
                    } else {
                        //cloudoflistview2.animate().y(value2).setDuration(300);
                    }
                }
            }
        });

        cloudoflistview2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //cloudoflistview2.setBackgroundResource(R.drawable.cloud_selected);
                DatabaseReference userSports = User.firebaseRef.child("users").child(User.uid).child("sports");
                userSports.addListenerForSingleValueEvent(new ValueEventListener() { // get user sports
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // Get user's favourite sports
                        ArrayList<String> favouriteSports = new ArrayList<>();
                        for (DataSnapshot sport : snapshot.getChildren()) {
                            if(!favouriteSports.contains(sport.getKey().toString()))
                                favouriteSports.add(sport.getKey().toString());
                        }
                        Intent createNewIntent = new Intent(getActivity(), CreateNewActivity.class);
                        createNewIntent.putStringArrayListExtra("favourite_sports", favouriteSports);
                        getActivity().startActivity(createNewIntent);
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                    }
                });
            }
        });
        if (mylistView != null) {
            if (myEventsList.size() == 0 && !eventHappeningNow) {
                // place holder no events created
            } else {
                mylistView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            mylistView.setAdapter(myadapter);
        }

         //Swipe */
        if (!addedSwipe) {
            final SwipeRefreshLayout mSwipeRefreshLayout1 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh1);
            mSwipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateListOfEvents(false);
                    mSwipeRefreshLayout1.setRefreshing(false);
                }
            });
            addedSwipe = true;
        }

        // Gather the events
        EventHandler eventHandler = EventHandler.getInstance();
        eventHandler.getEvents(getActivity(), myEventsList, favoriteSports, dialog, myadapter, fradapter, userRange, happeningNow);
    }
    // Start response from service:
    private BroadcastReceiver receiveStartResponse = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("Response");
            final Button changeStateButton = (Button) v.findViewById(R.id.stateChangeButton);
            final Chronometer timer = (Chronometer) v.findViewById(R.id.chronometer);

            if (msg == "0") { // Ok, start, location ok
                timer.setBase(SystemClock.elapsedRealtime() + 30000);
                timer.start();

                changeStateButton.setText("Stop");
                changeStateButton.setBackgroundColor(Color.parseColor("#BF3636"));
                getContext().getSharedPreferences("UserDetails", 0).edit().putString("HappeningNowEvent", "").commit();
                Toast.makeText(getContext(),
                        "Activity started. Do not close the application if you want to sweat on points.", Toast.LENGTH_LONG).show();
            } else { // location is not the right one
                Toast.makeText(getContext(),
                        "You are not in the right location!", Toast.LENGTH_LONG).show();
            }
        }
    };

    private BroadcastReceiver receiveIsHappening = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("isActive");

            // New event right now - display Happening now
            if (msg == "0") {
                //Toast.makeText(getContext(), "Awesome, you have an activity right now! :)", Toast.LENGTH_LONG).show();

                /// Get Current event
                eventPersistence = EventPersistence.getInstance();
                final Event currentEvent = eventPersistence.getCurrentEvent(getActivity());

                if (currentEvent != null) {
                    updateListOfEvents(false);
                }
            // Event ended
            } else {
                updateListOfEvents(false);
            }
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mServiceConn != null) {
            try {
                getActivity().unbindService(mServiceConn);
            } catch (IllegalArgumentException e) {
                System.out.println("Already unbounded! :)");
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mServiceConn != null) {
            try {
                getActivity().unbindService(mServiceConn);
            } catch (IllegalArgumentException e) {
                System.out.println("Already unbounded! :)");
            }
        }
    }
}
