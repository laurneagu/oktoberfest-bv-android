package larc.ludicon.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import larc.ludicon.Utils.Location_GPS.GPS_Positioning;
import larc.ludicon.Utils.Location_GPS.MyLocationListener;
import larc.ludicon.Utils.util.UniqueIDCreator;

public class CreateNewActivity extends Activity implements OnMapReadyCallback {

    static public int ASK_COORDS = 1000;
    static public int ASK_COORDS_DONE = 1001;

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private MyLocationListener locationListener;
    private LocationManager lm;
    private GoogleMap m_gmap;

    private double latitude=0;
    private double longitude=0;

    @Override
    public void onMapReady(GoogleMap map) {
        m_gmap = map;

        locationListener = new MyLocationListener();
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
        mapFragment.getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent goToNextActivity = new Intent(getApplicationContext(), GMapsFullActivity.class); //AskPreferences.class);
                startActivityForResult(goToNextActivity,ASK_COORDS);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                CreateNewActivity.this.startActivity(mainIntent);
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // DropDown for the sports

        Spinner spinner = (Spinner) findViewById(R.id.sports_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sports_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        spinner.setSelection(0, true);
        View v = spinner.getSelectedView();
        ((TextView)v).setTextColor(Color.parseColor("#000000"));

        TextView hello_message = (TextView) findViewById(R.id.hello_message_activity);
        hello_message.setText("Create activity");
    }

    public void onPrivacyButtonsClicked(View view) {
        // Is the button now selected
        TextView selected,notselected;

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
        df.setTimeZone(TimeZone.getTimeZone("gmt"));
        String gmtTime = df.format(calendar.getTime());
        map.put("date",  gmtTime);
        map.put("createdBy", User.uid);
        //map.put("creatorName", )
        Log.v("Name", User.firstName + User.lastName);
        //map.put("date",  java.text.DateFormat.getDateTimeInstance().format(calendar.getTime()));
        //Log.v("date", java.text.DateFormat.getDateTimeInstance().format(calendar.getTime()) );

        // Set privacy
        Button privacy = (Button) findViewById(R.id.publicBut);
        if (privacy.getAlpha() == 1) {
            map.put("privacy", "public");
        } else {
            map.put("privacy", "private");
        }


        // Set location
        final Map<String, Object> mapAux = new HashMap<>();
        if (latitude == 0 || longitude == 0) {
            mapAux.put("latitude",GPS_Positioning.getLatLng().latitude);
            mapAux.put("longitude", GPS_Positioning.getLatLng().longitude);

        }
        else{
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH );
            String addressName = "Parc Crangasi";
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if(addresses.size()>0) {
                    addressName = addresses.get(0).getAddressLine(0);
                }
            }
            catch(Exception exc){ addressName = "Unknown";}

            mapAux.put("latitude",latitude);
            mapAux.put("longitude", longitude);
            mapAux.put("name", addressName);
        }
        map.put("place", mapAux);
        // TODO Find a known place by coordinates

        // Set sport
        // TODO Get sport key
        Spinner spinner = (Spinner) findViewById(R.id.sports_spinner);
        String sportName = spinner.getSelectedItem().toString().toLowerCase().replaceAll("\\s+", "");
        map.put("sport", sportName);

        // Set users - currently only the one enrolled
        Map<String, Boolean> usersAttending = new HashMap<String, Boolean>();
        usersAttending.put(User.uid,true);
        map.put("users", usersAttending);

        // Check Events exists
        Firebase newEventRef = User.firebaseRef.child("events"); // check user
        newEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) { // flush the database everything is crazy
                    //User.firebaseRef.child("mesg").setValue("World is on fire");

                    /*
                    User.firebaseRef.child("sports").child("Football").child("id").setValue("0");
                    User.firebaseRef.child("sports").child("Volley").child("id").setValue("1");
                    User.firebaseRef.child("sports").child("Basketball").child("id").setValue("2");
                    User.firebaseRef.child("sports").child("Chess").child("id").setValue("3");
                    User.firebaseRef.child("sports").child("Ping Pong").child("id").setValue("4");
                    User.firebaseRef.child("sports").child("Tennis").child("id").setValue("5");
                    User.firebaseRef.child("sports").child("Cycling").child("id").setValue("6");
                    User.firebaseRef.child("sports").child("Jogging").child("id").setValue("7");
                    */
                }
                User.firebaseRef.child("events").child(id).setValue(map);

                // Each user has an "events" field which has a list of event ids
                Map<String,Object> ev =  new HashMap<String,Object>();
                ev.put(id, true);
                User.firebaseRef.child("users").child(User.uid).child("events").updateChildren(ev);


                try {
                    if(lm != null)
                        lm.removeUpdates(locationListener);
                } catch (SecurityException exc) {
                    exc.printStackTrace();
                }

                // Sanity checks
                lm = null;
                locationListener =null;

                jumpToMainActivity();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
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

        // Sanity checks
        lm = null;
        locationListener =null;
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        latitude = data.getDoubleExtra("latitude", 0);
        longitude = data.getDoubleExtra("longitude", 0);

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
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, CreateNewActivity.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), CreateNewActivity.this);

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
