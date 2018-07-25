/*
package larc.ludiconprod.Activities;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v7.app.AlertDialog.Builder;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import larc.ludiconprod.Model.ChatHandler;
import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.FriendsList;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.Location.ActivitiesLocationListener;
import larc.ludiconprod.Utils.Sport;
import larc.ludiconprod.Utils.util.DateManager;
import larc.ludiconprod.Utils.util.UniqueIDCreator;
import larc.ludiconprod.Utils.util.Utils;

public class CreateNewActivity extends Activity implements OnMapReadyCallback {

    static public int ASK_COORDS = 1000;
    static public int ASK_COORDS_DONE = 1001;
    private static final String FIREBASE_URL = "https://ludicon.firebaseio.com/";
    static public ArrayList<String> favouriteSports;

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private ActivitiesLocationListener locationListener;
    private LocationManager lm;
    private GoogleMap m_gmap;
    private Switch switchPrivacy;
    private double latitude = 0;
    private double longitude = 0;
    private int isOfficial = 0;
    ArrayAdapter<String> versionNames;

    MultiAutoCompleteTextView simpleMultiAutoCompleteTextView;
    ArrayList<String> androidVersionNames=new ArrayList<String>();
    ArrayList<String> FriendsIdList=new ArrayList<String>();
    ArrayList<String> RemainFriendsIdList=new ArrayList<String>();
    ArrayList<String> FriendNameList=new ArrayList<String>();
    ArrayList<String> FriendChatList=new ArrayList<String>();
    private int mPreviousLength;
    private boolean mBackSpace;

    private String addressName = ""; //default address
    String originalText;
    int end=-1;
    int first=-1;
    int position=0;
    boolean texttChoice=false;
    int contor=0;
    int initial=0;
    ArrayList<String> editTextContent=new ArrayList<String>();
    ArrayList<Integer> itemToRemove=new ArrayList<Integer>();

    private List<String> sports = new ArrayList<String>() {{
        add("football");
        add("volleyball");
        add("basketball");
        add("squash");
        add("pingpong");
        add("tennis");
        add("cycling");
        add("jogging");
        add("gym");
        add("other");
        ;
    }};

    @Override
    public void onMapReady(GoogleMap map) {
        m_gmap = map;

//        locationListener = new ActivitiesLocationListener(getApplication());
//        locationListener.BindMap(map);
//        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        try {
//            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, locationListener);
//            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, locationListener);
//
//            // Creating a criteria object to retrieve provider
//            Criteria criteria = new Criteria();
//
//            // Getting the name of the best provider
//            String provider = lm.getBestProvider(criteria, true);
//
//        } catch (SecurityException exc) {
//            exc.printStackTrace();
//        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        SharedPreferences sharedPref = getApplication().getSharedPreferences("UserDetails", 0);
        String latString, longString;

        latString = sharedPref.getString("current_latitude", null);
        longString = sharedPref.getString("current_longitude", null);

        sharedPref = getApplication().getSharedPreferences("LocationPrefs", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("sel_latitude", null);
        editor.putString("sel_longitude", null);
        editor.commit();

        double lati = 0;
        double longi = 0;
        try {
            lati = Double.parseDouble(latString);
            longi = Double.parseDouble(longString);
        } catch (NullPointerException e) {
            e.printStackTrace();
//            Context context = getApplicationContext();
//            int duration = Toast.LENGTH_LONG;
//
//            Toast toast = Toast.makeText(context, latString + longString + "  Error", duration);
//            toast.show();
        }

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati, longi), 15));
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

    private final CreateNewActivity myAct = this;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // Hide App bar
            // If the Android version is lower than Jellybean, use this call to hide
            // the status bar.
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            // remove title
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_create_new);
            //String currentEventUID = "";
            String json = getSharedPreferences("UserDetails", 0).getString("friendsList", null);
            Type type = new TypeToken<ArrayList<FriendsList>>() {
            }.getType();
            ArrayList<FriendsList> myCurrentFriends = new Gson().fromJson(json, type);
            for (int i = 0; i < myCurrentFriends.size(); i++) {
                androidVersionNames.add(myCurrentFriends.get(i).name);
                FriendsIdList.add(myCurrentFriends.get(i).id);
                FriendNameList.add(myCurrentFriends.get(i).name);
            }
             simpleMultiAutoCompleteTextView = (MultiAutoCompleteTextView) findViewById(R.id.simpleMultiAutoCompleteTextView);
           versionNames = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, androidVersionNames);
            simpleMultiAutoCompleteTextView.setAdapter(versionNames);

// set threshold value 1 that help us to start the searching from first character
            simpleMultiAutoCompleteTextView.setThreshold(1);
// set tokenizer that distinguish the various substrings by comma
            simpleMultiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
            simpleMultiAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = (String) parent.getItemAtPosition(position);

                    for (int i = 0; i < androidVersionNames.size(); i++) {
                        if (selectedItem.equalsIgnoreCase(androidVersionNames.get(i))) {
                            versionNames.remove(selectedItem);
                            editTextContent.add(selectedItem+", ");



                        }



                    }
                }
            });
            simpleMultiAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    mPreviousLength = s.length();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    mBackSpace = mPreviousLength > s.length();


                    position=simpleMultiAutoCompleteTextView.getSelectionEnd();

                    if (mBackSpace && position !=0 ) {

                        int itemCount=0;
                        ArrayList<Integer> endPosition=new ArrayList<Integer>();
                         int arrayPosition=0;
                        for(int i=0;i < editTextContent.size();i++){

                            arrayPosition=arrayPosition+editTextContent.get(i).length();
                            endPosition.add(arrayPosition);
                        }
                        for(int i=1;i < endPosition.size();i++) {
                            if(position > endPosition.get(i-1) && position<=endPosition.get(i)){
                                itemCount=i;
                        }
                        break;
                        }

                        versionNames.add(editTextContent.get(itemCount).substring(0,editTextContent.get(itemCount).length()-2));
                        editTextContent.remove(itemCount);
                        String textField="";
                        for(int i=0;i<editTextContent.size();i++){
                            textField=textField+editTextContent.get(i);
                        }
                        simpleMultiAutoCompleteTextView.setText(textField);
                        simpleMultiAutoCompleteTextView.setSelection(textField.length());

                    }

                }
            });


            // Get favourite sports from intent
            favouriteSports = getIntent().getStringArrayListExtra("favourite_sports");
            if (favouriteSports == null)
                favouriteSports = new ArrayList<>();

            // Left side panel initializing
            // mDrawerList = (ListView) findViewById(R.id.leftMenu);
            //initializeLeftSidePanel();

            //User.setImage();

            // User picture and name for HEADER MENU
            // User picture and name for HEADER MENU
            Typeface segoeui = Typeface.createFromAsset(getAssets(), "fonts/seguisb.ttf");
            switchPrivacy = (Switch) findViewById(R.id.switchPrivacy);
            TextView userName = (TextView) findViewById(R.id.userName);
            userName.setText(User.getFirstName(getApplicationContext()));
            userName.setTypeface(segoeui);

            TextView userSportsNumber = (TextView) findViewById(R.id.userSportsNumber);
            userSportsNumber.setText(User.getNumberOfSports(getApplicationContext()));
            userSportsNumber.setTypeface(segoeui);

 ImageView userPic = (ImageView) findViewById(R.id.userPicture);
        Drawable d = new BitmapDrawable(getResources(), User.image);
        userPic.setImageDrawable(d);
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainVechi.class);
                CreateNewActivity.this.startActivity(mainIntent);
            }
        });



            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            // Clear auto scroll
            final EditText editTextDesc = (EditText) findViewById(R.id.DescriptionInput);

            ScrollView scroll = (ScrollView) findViewById(R.id.scroll);
            scroll.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (editTextDesc.hasFocus()) {
                        editTextDesc.clearFocus();
                    }
                    return false;
                }
            });

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



            selectedSportButton = (Button) findViewById(R.id.selectedSportButton);
            String uri = "@drawable/" + favouriteSports.get(0);
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            selectedSportButton.setBackgroundResource(imageResource);
            selectedSportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference userSports = User.firebaseRef.child("users").child(User.uid).child("sports");
                    userSports.addListenerForSingleValueEvent(new ValueEventListener() { // get user sports
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            showGridSportsDialog();
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                        }
                    });
                }
            });

            TextView hello_message = (TextView) findViewById(R.id.hello_message_activity);
            hello_message.setText("");

            // Events on buttons of Max players capacity

            final EditText maxCapacityET = (EditText) findViewById(R.id.maxPlayersET);

            final Button removePeople = (Button) findViewById(R.id.removePeople);

            removePeople.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        removePeople.setAlpha((float) 0.3);

                        int currValue = Integer.parseInt(maxCapacityET.getText().toString());
                        if (currValue >= 3)
                            currValue--;
                        maxCapacityET.setText(currValue + "");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        removePeople.setAlpha((float) 1);
                    }
                    return true;
                }
            });

            //// Create event in header menu
            createEvent = (ImageButton) findViewById(R.id.header_button);
            createEvent.setVisibility(View.VISIBLE);
            createEvent.setBackgroundResource(R.drawable.save);
            createEvent.getLayoutParams().height = 100;
            createEvent.getLayoutParams().width = 100;

            createEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Builder builder = new Builder(CreateNewActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle("Add an event")
                            .setMessage("Are you sure you want to add an event?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setNegativeButton("NO", null)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {


                                    DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(User.uid).child("chats");
                                    firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(int i=0;i<RemainFriendsIdList.size();i++){
                                                if(dataSnapshot.hasChild(RemainFriendsIdList.get(i))){
                                                    FriendChatList.add(dataSnapshot.child(RemainFriendsIdList.get(i)).getValue().toString());

                                                }
                                                else{
                                                    FriendChatList.add("First chat");
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });




                                    if (!OnCreateEvent()) {
                                        createEvent.setEnabled(true);
                                        createEvent.setClickable(true);
                                        createEvent.setAlpha((float) 1);
                                        return;
                                    }


                                    SharedPreferences sharedPref = myAct.getSharedPreferences("LocationPrefs", 0);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("sel_latitude", null);
                                    editor.putString("sel_longitude", null);
                                    editor.commit();
                                    createEvent.setAlpha((float) 0.3);
                                    createEvent.setClickable(false);
                                }
                            }).show();
                }
            });

            final Button addPeople = (Button) findViewById(R.id.addPeople);

            addPeople.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        addPeople.setAlpha((float) 0.3);

                        int currValue = Integer.parseInt(maxCapacityET.getText().toString());
                        if (currValue <= 50)
                            currValue++;
                        maxCapacityET.setText(currValue + "");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        addPeople.setAlpha((float) 1);
                    }
                    return true;
                }
            });


            /// Date and Time holders

            editTextDateHolder = (EditText) findViewById(R.id.calendarHolderET);
            editTextTimeHolder = (EditText) findViewById(R.id.timeHolderET);

            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel();
                }

            };

            String myFormat = "dd-MMM-yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
            editTextDateHolder.setText(sdf.format(myCalendar.getTime()));
            editTextDateHolder.setFocusable(false);
            editTextDateHolder.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    // TODO Auto-generated method stub
                    DatePickerDialog dpd = new DatePickerDialog(CreateNewActivity.this, R.style.DialogTheme, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));
                    dpd.show();
                }
            });

            // Time picker
            int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = myCalendar.get(Calendar.MINUTE);
            editTextTimeHolder.setText((hour < 9 ? "0" : "") + hour + ":" + (minute < 9 ? "0" : "") + minute);
            editTextTimeHolder.setFocusable(false);
            editTextTimeHolder.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                    int minute = myCalendar.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(CreateNewActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            editTextTimeHolder.setText((selectedHour < 9 ? "0" : "") + selectedHour + ":" + (selectedMinute < 9 ? "0" : "") + selectedMinute);
                            myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                            myCalendar.set(Calendar.MINUTE, selectedMinute);

                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();

                }
            });
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private ImageButton createEvent;

    private Button selectedSportButton;
    private ArrayList<Sport> sportsList = new ArrayList<Sport>();
    private MyCustomAdapter dataAdapter = null;

    private void showGridSportsDialog() {
        if (alertDialog == null) {
            // Prepare grid view
            gridView = new GridView(this);

            int count = 0;
            for (String sport : sports) {
                // Check if sport is selected as favourite
                if(favouriteSports.contains(sport))
                {
                    String uri = "@drawable/" + sport;
                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                    Drawable res1 = getResources().getDrawable(imageResource);
                    sportsList.add(new Sport(sport, Integer.toString(count),
                            false, ((BitmapDrawable) res1).getBitmap()));
                    count++;
                }
            }

            //create an ArrayAdaptor from the Sport Array
            dataAdapter = new MyCustomAdapter(getApplicationContext(), R.layout.sport_info, sportsList);

            // Assign adapter to ListView
            gridView.setAdapter(dataAdapter);
            gridView.setNumColumns(2);

            // Set grid view to alertDialog
            builder = new AlertDialog.Builder(this);
            builder.setView(gridView);
            //builder.setTitle("Pick your favourite sport");
            alertDialog = builder.show();

            //dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_light);
        }

        alertDialog.show();
    }

    GridView gridView;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    EditText editTextDateHolder;
    EditText editTextTimeHolder;
    final Calendar myCalendar = Calendar.getInstance();

    private void updateLabel() {

        String myFormat = "dd/MMM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        editTextDateHolder.setText(sdf.format(myCalendar.getTime()));
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


    public boolean checkEventDateIsNotInPast(Date creationDate) {
        Date now = new Date();
        if (creationDate.before(now)) {
            Toast.makeText(this.getApplicationContext(), "You can't create an event in the past",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    public boolean isSameDay(Date a, Date b) {
        if (a.getYear() != b.getYear())
            return false;
        if (a.getMonth() != b.getMonth())
            return false;
        if (a.getDay() != b.getDay())
            return false;
        return true;
    }

    public boolean OnCreateEvent() {
        try {
            Calendar calendar = myCalendar;

            UniqueIDCreator idCreator = new UniqueIDCreator();
            final String id = idCreator.nextSessionId();

            final Map<String, Object> map = new HashMap<String, Object>();

            // Set date it will be played
            // TODO GMT format
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale("English"));
            df.setTimeZone(TimeZone.getTimeZone("Europe/Bucharest"));

            String gmtTime = df.format(calendar.getTime());
            final Date creationDate = calendar.getTime();

            if (checkEventDateIsNotInPast(creationDate))
                return false;

            createEvent.setEnabled(false);
            createEvent.setClickable(false);
            createEvent.setAlpha((float) 0.2);
            //createEvent.setText("Creating ..");
            Toast.makeText(getApplicationContext(), "Creating..", Toast.LENGTH_LONG).show();

            ProgressBar pb = (ProgressBar) findViewById(R.id.marker_progress);
            pb.setVisibility(View.VISIBLE);

            map.put("date", DateManager.convertFromTextToSeconds(gmtTime));
            map.put("createdBy", User.uid);
            map.put("active", true);
            map.put("creatorName", User.name);
            map.put("creatorImage", User.profilePictureURL);


            Log.v("Name", User.firstName + User.lastName);

            if(switchPrivacy.isChecked()) {
                map.put("privacy", "private");
            }
            else{
                map.put("privacy", "public");
            }

            // Set location
            final Map<String, Object> mapAux = new HashMap<>();

            // location not provided
            if (latitude == 0 && longitude == 0) {

                Toast.makeText(getApplicationContext(), "Please tap on the map and choose a place to play!", Toast.LENGTH_LONG).show();
                createEvent.setEnabled(true);
                createEvent.setClickable(true);
                createEvent.setAlpha((float) 1);

                return false;

//                mapAux.put("latitude", GPS_Positioning.getLatLng().latitude);
//                mapAux.put("longitude", GPS_Positioning.getLatLng().longitude);
//
//                latitude = GPS_Positioning.getLatLng().latitude;
//                longitude = GPS_Positioning.getLatLng().longitude;
//
//                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
//
//                try {
//                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
//                    if (addresses.size() > 0) {
//                        if (addressName == null || addressName.equals("")) {
//                            addressName = addresses.get(0).getAddressLine(0);
//                        }
//                    }
//                } catch (Exception exc) {
//                    addressName = "Unknown";
//                }
//
//                mapAux.put("name", addressName);

            } else {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        if (addressName == null || addressName.equals("")) {
                            addressName = addresses.get(0).getAddressLine(0);
                        }
                    }
                } catch (Exception exc) {
                    addressName = "Unknown";
                }

                mapAux.put("latitude", latitude);
                mapAux.put("longitude", longitude);
                mapAux.put("name", addressName);
            }
            // If the user will get points or not for the event
            map.put("isOfficial", isOfficial);

            map.put("place", mapAux);

            EditText number = (EditText) findViewById(R.id.maxPlayersET);
            int maxPlayers = Integer.parseInt(number.getText().toString());

            EditText editTextDesc = (EditText) findViewById(R.id.DescriptionInput);
            String description = editTextDesc.getText().toString();

            // Event extra info:
            map.put("roomCapacity", maxPlayers);
            map.put("priority", 0);
            map.put("description", description);
            map.put("message", null);




            // Set sport
            // TODO Get sport key
            String sportName = sports.get(sportIndex);
            //spinner.getSelectedItem().toString().toLowerCase().replaceAll("\\s+", "");
            map.put("sport", sportName);

            // Set users - currently only the one enrolled
            Map<String, Boolean> usersAttending = new HashMap<String, Boolean>();
            usersAttending.put(User.uid, true);
            map.put("users", usersAttending);

            // Check Events exists
            DatabaseReference newEventRef = User.firebaseRef.child("events"); // check user
            newEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.getValue() == null) { // flush the database everything is crazy
                        //User.firebaseRef.child("mesg").setValue("World is on fire");

                        // Script to refresh sports in the database
                    User.firebaseRef.child("sports").child("Football").child("id").setValue("0");
                    User.firebaseRef.child("sports").child("volleyball").child("id").setValue("1");
                    User.firebaseRef.child("sports").child("Basketball").child("id").setValue("2");
                    User.firebaseRef.child("sports").child("Chess").child("id").setValue("3");
                    User.firebaseRef.child("sports").child("Ping Pong").child("id").setValue("4");
                    User.firebaseRef.child("sports").child("Tennis").child("id").setValue("5");
                    User.firebaseRef.child("sports").child("Cycling").child("id").setValue("6");
                    User.firebaseRef.child("sports").child("Jogging").child("id").setValue("7");


                    }

                    // Get the events from Shared Prefs
                    String connectionsJSONString = getSharedPreferences("UserDetails", 0).getString("myEvents", null);
                    Type type = new TypeToken<ArrayList<Event>>() {
                    }.getType();
                    ArrayList<Event> myCurrentEvents = new Gson().fromJson(connectionsJSONString, type);

                    int numberOfEvents = 0;
                    boolean isSameDate = false;
                    ;
                    Date now = new Date();
                    for (Event event : myCurrentEvents) {
                        if (event.date.before(now))
                            continue;
                        if (isSameDay(creationDate, event.date) && Math.abs(event.date.getHours() - creationDate.getHours()) <= 2) {
                            isSameDate = true;
                            break;
                        }
                        if (isSameDay(creationDate, event.date))
                            numberOfEvents++;
                        if (numberOfEvents >= 3 || isSameDate)
                            break;
                    }

                    if (isSameDate) {
                        Toast.makeText(getApplicationContext(), "You have scheduled an event at this date already ! Please check your agenda !", Toast.LENGTH_LONG).show();
                        createEvent.setEnabled(true);
                        createEvent.setClickable(true);
                        createEvent.setAlpha((float) 1);
                    } else if (numberOfEvents >= 3) {
                        Toast.makeText(getApplicationContext(), "You already reached the limit of 3 events on this day. Please pick another day !", Toast.LENGTH_LONG).show();
                        createEvent.setEnabled(true);
                        createEvent.setClickable(true);
                        createEvent.setAlpha((float) 1);
                    } else {

                        User.firebaseRef.child("events").child(id).setValue(map);
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

                        for(int i=0;i<FriendNameList.size();i++){
                            boolean contains=false;
                            for(int j=0;j<editTextContent.size();j++) {
                                if (editTextContent.get(j).contains(FriendNameList.get(i))){
                                    contains=true;
                                }



                            }
                            if(!contains){
                                itemToRemove.add(0);
                            }
                            else{
                                itemToRemove.add(1);
                            }

                        }
                        for(int i=0;i<itemToRemove.size();i++) {
                            if(itemToRemove.get(i) == 1){
                                RemainFriendsIdList.add(FriendsIdList.get(i));
                            }

                        }

                        // Get instance of ChatHandler
                        final ChatHandler chatHandler = ChatHandler.getInstance();

                        // Get list of chats for the current user
                        Task task  = chatHandler.getListOfChats(User.uid);
                        task.addOnCompleteListener(CreateNewActivity.this, new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                Dictionary<String, String> uidsChat= (Dictionary<String,String>)task.getResult();
                                for(int i=0;i<RemainFriendsIdList.size();i++){
                                    if(chatHandler.isFirstConversation(uidsChat.keys(),RemainFriendsIdList.get(i))){
                                        FriendChatList.add("First chat");
                                    }else{
                                        FriendChatList.add(uidsChat.get(RemainFriendsIdList.get(i)));
                                    }
                                }

                                for(int i=0;i<FriendChatList.size();i++){
                                    if(FriendChatList.get(i).equalsIgnoreCase("First chat")){
                                        String chatId =chatHandler.generateChat(RemainFriendsIdList.get(i),User.uid);
                                        chatHandler.sendMessage(chatId,User.uid, User.name,"[%##"+id.toString()+"##%]");
                                    }else
                                    {
                                        chatHandler.sendMessage(FriendChatList.get(i),User.uid, User.name,"[%##"+id.toString()+"##%]");
                                    }

                                }
                            }
                        });


                        jumpToMainActivity();
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            });





        } catch (Exception exc) {
            Utils.quit();
        }

        return true;
    }

*
     * Method that jumps to the MainActivityVechi


    public void jumpToMainActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 5 seconds
                // Actions to do after 5 seconds
                //Intent goToNextActivity = new Intent(getApplicationContext(), MainVechi.class); //AskPreferences.class);
               // startActivity(goToNextActivity);
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
        } catch (Exception ex) {
            // the location is not any more initialized - lm is null
        }

        // Sanity activities
        lm = null;
        locationListener = null;

        SharedPreferences sharedPref = myAct.getSharedPreferences("LocationPrefs", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("sel_latitude", null);
        editor.putString("sel_longitude", null);
        editor.commit();

        finish();

        Intent toMain = new Intent(this, MainVechi.class);
        toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMain);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
            isOfficial = data.getIntExtra("isOfficial", 0);
            addressName = data.getStringExtra("address");

            String comment = data.getStringExtra("comment");
            if (comment != null && !comment.equalsIgnoreCase("")) {
                Toast.makeText(getApplication(), comment, Toast.LENGTH_LONG).show();
            }

            // Set also the location name selected

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    if (addressName == null || addressName.equals("")) {
                        addressName = addresses.get(0).getAddressLine(0);
                    }
                }
            } catch (Exception exc) {
                addressName = "Unknown";
            }

            TextView locationHint = (TextView) findViewById(R.id.locationHint);
            locationHint.setText("Will play at : " + addressName);
            locationHint.setTextColor(Color.parseColor("#4f2f4f"));
        }

        try {
            if (lm != null)
                lm.removeUpdates(locationListener);
        } catch (SecurityException exc) {
            exc.printStackTrace();
        }

        // Sanity checks
        lm = null;
        locationListener = null;

        LatLng latLng = new LatLng(latitude, longitude);

        m_gmap.clear();

        m_gmap.addMarker(new MarkerOptions()
                .position(latLng)
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.football))
                .title("This is your selected area"));
        m_gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences sharedPref = getSharedPreferences("LocationPrefs", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("curr_latitude", null);
        editor.putString("curr_longitude", null);
        editor.commit();

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

    int sportIndex = 0;

    private class MyCustomAdapter extends ArrayAdapter<Sport> {

        private ArrayList<Sport> sportsList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Sport> sList) {
            super(context, textViewResourceId, sList);
            this.sportsList = new ArrayList<>();
            this.sportsList.addAll(sList);
        }

        private class ViewHolder {
            RelativeLayout rl;
            ImageView image;
            TextView text;
            CheckBox box;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.sport_info, null);

                holder = new ViewHolder();
                holder.rl = (RelativeLayout) convertView.findViewById(R.id.irLayout);
                holder.box = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.text = (TextView) convertView.findViewById(R.id.code);
                holder.image = (ImageView) convertView.findViewById(R.id.icon);

                convertView.setTag(holder);

                holder.box.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Sport sport = (Sport) cb.getTag();
                        RelativeLayout rl = (RelativeLayout) cb.getParent();
                        sport.setSelected(cb.isChecked());

                        //sportsList = new ArrayList<Sport>();
                        sportIndex = Integer.parseInt(sport.id);
                        selectedSportButton.setBackground(new BitmapDrawable(getResources(), sport.icon));

                        alertDialog.dismiss();
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Sport sport = sportsList.get(position);
            //holder.text.setText("");
            holder.text.setText(sport.name);
            holder.box.setText("");
            //holder.box.setText(sport.name);
            holder.box.setChecked(sport.isChecked);
            holder.box.setTag(sport);
            holder.text.setTextColor(getResources().getColor(R.color.white));
            if (sportIndex==position) {
                holder.rl.setBackgroundColor(getResources().getColor(R.color.bg1));
                holder.image.setImageBitmap(sport.icon);
                holder.box.setTextColor(getResources().getColor(R.color.white));
                holder.box.setAlpha((float) 0.9);
            } else {


            holder.box.setTextColor(getResources().getColor(R.color.white));
            holder.rl.setBackgroundColor(getResources().getColor(R.color.bg2));
            holder.image.setImageBitmap(sport.icon);
            holder.box.setAlpha((float) 0.7);
            //  }

            return convertView;

        }
    }
}


*/
