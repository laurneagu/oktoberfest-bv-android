package larc.ludicon.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import larc.ludicon.Adapters.LeftPanelItemClicker;
import larc.ludicon.Adapters.LeftSidePanelAdapter;
import larc.ludicon.R;
import larc.ludicon.UserInfo.ActivityInfo;
import larc.ludicon.UserInfo.User;
import larc.ludicon.Utils.Event;
import larc.ludicon.Utils.Location.GPS_Positioning;
import larc.ludicon.Utils.Location.ActivitiesLocationListener;
import larc.ludicon.Utils.util.UniqueIDCreator;
import larc.ludicon.Utils.util.Utils;

public class EditEventActivity extends Activity implements OnMapReadyCallback {

    static public int ASK_COORDS = 1000;
    static public int ASK_COORDS_DONE = 1001;

    private static final String FIREBASE_URL = "https://ludicon.firebaseio.com/";

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private ActivitiesLocationListener locationListener;
    private LocationManager lm;
    private GoogleMap m_gmap;
    private LatLng latLNG;

    private double latitude = 0;
    private double longitude = 0;
    private int isOfficial = 0;

    private String addressName = ""; //default address

    @Override
    public void onMapReady(GoogleMap map) {
        m_gmap = map;
        // Set location of event
        m_gmap.addMarker(new MarkerOptions()
                .position(latLNG)
                .title("This is your selected area"));
        locationListener = new ActivitiesLocationListener(getApplication());
        locationListener.BindMap(map);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, locationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, locationListener);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = lm.getBestProvider(criteria, true);

        } catch (SecurityException exc) {
            exc.printStackTrace();
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Intent goToNextActivity = new Intent(getApplicationContext(), GMapsFullActivity.class); //AskPreferences.class);
                        startActivityForResult(goToNextActivity, ASK_COORDS);
                    }
                });
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);

        // Left side panel initializing
        mDrawerList = (ListView) findViewById(R.id.leftMenu);
        initializeLeftSidePanel();

        User.setImage();

        // User picture and name for HEADER MENU
        TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText(User.getFirstName(getApplicationContext()) + " " + User.getLastName(getApplicationContext()));

        ImageView userPic = (ImageView) findViewById(R.id.userPicture);
        Drawable d = new BitmapDrawable(getResources(), User.image);
        userPic.setImageDrawable(d);
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                EditEventActivity.this.startActivity(mainIntent);
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // TODO - Receive sent intents - eventID, location

        // Set event's date and time in Date/TimePicker
        Date eventDate = new Date(getIntent().getStringExtra("eventDate"));
        DatePicker datePicker = (DatePicker) findViewById(R.id.date_picker);
        datePicker.updateDate(1900 + eventDate.getYear(),eventDate.getMonth(),eventDate.getDate());
        TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);
        timePicker.setCurrentHour(eventDate.getHours());
        timePicker.setCurrentMinute(eventDate.getMinutes());

        // DropDown for the sports

        Spinner spinner = (Spinner) findViewById(R.id.sports_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sports_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // Set selection of spinner to event's sport
        int length = adapter.getCount();
        String eventSport = getIntent().getStringExtra("sport");
        for ( int i = 0; i < length; i++){
            //Log.v("spinner item", spinner.getItemAtPosition(i) + " vs " + eventSport);
            if ( spinner.getItemAtPosition(i).toString().equalsIgnoreCase(eventSport) )
            {   spinner.setSelection(i); Log.v("selected",i + " ");break;}}

        // TODO - Are these 2 lines important?
        //View v = spinner.getSelectedView();
        //((TextView)v).setTextColor(Color.parseColor("#000000"));
/*
        // Set privacy of event
        String privacy = getIntent().getStringExtra("eventPrivacy");
        if ( privacy.equalsIgnoreCase("public"))
        {
            TextView selected,notselected;
            selected = (TextView)findViewById(R.id.publicBut);
            selected.setAlpha(1);

            notselected = (TextView)findViewById(R.id.privateBut);
            notselected.setAlpha((float)0.7);
        }
        else
        {
            TextView selected,notselected;
            selected = (TextView)findViewById(R.id.privateBut);
            selected.setAlpha(1);

            notselected = (TextView)findViewById(R.id.publicBut);
            notselected.setAlpha((float)0.7);
        }
        */
        double longitude = Double.parseDouble(getIntent().getStringExtra("longitude"));
        double latitude = Double.parseDouble(getIntent().getStringExtra("latitude"));
        latLNG = new LatLng(latitude,longitude);


        TextView hello_message = (TextView) findViewById(R.id.hello_message_activity);
        hello_message.setText("Edit Event");
        Button editBtn = (Button)findViewById(R.id.createEvent);
        editBtn.setText("Edit");
        }
        catch(Exception exc) {
            Utils.quit();
        }
    }
/*
    public void onPrivacyButtonsClicked(View view) {
        // Is the button now selected
        TextView selected, notselected;

        switch(view.getId()) {
            case R.id.publicBut:
                selected = (TextView)findViewById(R.id.publicBut);
                selected.setAlpha(1);

                notselected = (TextView)findViewById(R.id.privateBut);
                notselected.setAlpha((float)0.7);
                break;
            case R.id.privateBut:
                selected = (TextView)findViewById(R.id.privateBut);
                selected.setAlpha(1);

                notselected = (TextView)findViewById(R.id.publicBut);
                notselected.setAlpha((float)0.7);
                break;
        }
    }
    */
    public boolean checkEventDateIsNotInPast(Date creationDate)
    {
        Date now = new Date();
        if ( creationDate.before(now) ) {
            Toast.makeText(this.getApplicationContext(), "You can't create an event in the past",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    public void OnCreateEvent(View view) {
        DatePicker datePicker = (DatePicker) findViewById(R.id.date_picker);
        TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);

        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                timePicker.getCurrentHour(),
                timePicker.getCurrentMinute());

        UniqueIDCreator idCreator = new UniqueIDCreator();
        final String id = idCreator.nextSessionId();

        final Map<String, Object> map = new HashMap<String, Object>();


        // Set date it will be played
        // TODO GMT format
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale("English"));
        df.setTimeZone(TimeZone.getTimeZone("Europe/Bucharest"));

        String gmtTime = df.format(calendar.getTime());
        final Date creationDate = calendar.getTime();

        if ( checkEventDateIsNotInPast(creationDate) )
            return;

        // Stop button to be called more than once
        Button createEvent = (Button) findViewById(R.id.createEvent);
        createEvent.setEnabled(false);
        createEvent.setClickable(false);
        createEvent.setText("Editing ..");
        createEvent.setBackgroundColor(Color.TRANSPARENT);
        createEvent.setTextColor(Color.BLUE);
        ProgressBar pb = (ProgressBar)findViewById(R.id.marker_progress);
        pb.setVisibility(View.VISIBLE);

        map.put("date",  gmtTime);
        map.put("createdBy", User.uid);
        map.put("active",true);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(User.uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren() )
                {
                    if ( data.getKey().equalsIgnoreCase("name") ) {
                        map.put("creatorName", data.getValue().toString());
                        Log.v("CreatorName", data.getValue().toString());
                    }
                    if ( data.getKey().equalsIgnoreCase("profileImageURL") ) {
                        map.put("creatorImage", data.getValue().toString());
                        Log.v("CreatorImage", data.getValue().toString());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });

        try{
            Thread.sleep(200,1);
        }
        catch(InterruptedException exc){}

        Log.v("Name", User.firstName + User.lastName);
        //map.put("date",  java.text.DateFormat.getDateTimeInstance().format(calendar.getTime()));
        //Log.v("date", java.text.DateFormat.getDateTimeInstance().format(calendar.getTime()) );
/*
        // Set privacy
        Button privacy = (Button) findViewById(R.id.publicBut);
        if (privacy.getAlpha() == 1) {
            map.put("privacy", "public");
        } else {
            map.put("privacy", "private");
        }
*/
        map.put("privacy", "public");

        // Set location
        final Map<String, Object> mapAux = new HashMap<>();

        // location not provided
        if (latitude == 0 || longitude == 0) {
            mapAux.put("latitude",GPS_Positioning.getLatLng().latitude);
            mapAux.put("longitude", GPS_Positioning.getLatLng().longitude);
        }
        else{
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH );

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if(addresses.size()>0) {
                    if(addressName.equals("")) {
                        addressName = addresses.get(0).getAddressLine(0);
                    }
                }
            }
            catch(Exception exc){ addressName = "Unknown";}

            mapAux.put("latitude",latitude);
            mapAux.put("longitude", longitude);
            mapAux.put("name", addressName);
        }
        // If the user will get points or not for the event
        map.put("isOfficial",isOfficial);

        map.put("place", mapAux);

        // Set sport
        // TODO Get sport key
        Spinner spinner = (Spinner) findViewById(R.id.sports_spinner);
        String sportName = spinner.getSelectedItem().toString().toLowerCase().replaceAll("\\s+", "");
        map.put("sport", sportName);

        // Set users - currently only the one enrolled
        Map<String, Boolean> usersAttending = new HashMap<String, Boolean>();
        usersAttending.put(User.uid,true);
        map.put("users", usersAttending);



                    DatabaseReference eventRef = User.firebaseRef.child("events").child(getIntent().getStringExtra("eventID"));
                    eventRef.updateChildren(map);
                    // Each user has an "events" field which has a list of event ids
                    Map<String, Object> ev = new HashMap<String, Object>();
                    Map<String, Object> inEv = new HashMap<>();
                    inEv.put("participation", true);
                    inEv.put("points", 0);
                    ev.put(id, inEv);
                    User.firebaseRef.child("users").child(User.uid).child("events").updateChildren(ev);

                    try {
                        if (lm != null)
                            lm.removeUpdates(locationListener);
                    } catch (SecurityException exc) {
                        exc.printStackTrace();
                    }

                    // Sanity checks
                    lm = null;
                    locationListener = null;

                    jumpToMainActivity();
    }

    /**
     * Method that jumps to the MainActivity
     */
    public void jumpToMainActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 5 seconds
                Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class); //AskPreferences.class);
                startActivity(goToNextActivity);
                finish();
            }
        }, 2000); // Delay time for transition to next activity -> insert any time wanted here instead of 5000
    }

    @Override
    public void onBackPressed() {

        try {
            lm.removeUpdates(locationListener);
        } catch (SecurityException exc) {
            exc.printStackTrace();
        }

        // Sanity activities
        lm = null;
        locationListener =null;
        finish();

        Intent toMain = new Intent(this,MainActivity.class);
        toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMain);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
            isOfficial = data.getIntExtra("isOfficial", 0);
            addressName = data.getStringExtra("address");

            String comment = data.getStringExtra("comment");

            Toast.makeText(getApplication(), comment, Toast.LENGTH_LONG).show();
        }

        try {
            if(lm != null)
                lm.removeUpdates(locationListener);
        } catch (SecurityException exc) {
            exc.printStackTrace();
        }

        // Sanity checks
        lm = null;
        locationListener =null;

        LatLng latLng = new LatLng(latitude, longitude);

        m_gmap.clear();

        m_gmap.addMarker(new MarkerOptions()
                .position(latLng)
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.football))
                .title("This is your selected area"));
        m_gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    // Left side menu

    public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_createnew);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, EditEventActivity.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), EditEventActivity.this);

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

        return super.onOptionsItemSelected(item);
    }
}
