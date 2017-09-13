package larc.ludiconprod.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Adapters.CustomSpinner;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.EventDetails;
import larc.ludiconprod.Utils.Friend;
import larc.ludiconprod.Utils.Location.GPSTracker;

import static larc.ludiconprod.Activities.IntroActivity.decodeBase64;
import static larc.ludiconprod.Activities.InviteFriendsActivity.numberOfOfflineFriends;

/**
 * Created by ancuta on 7/31/2017.
 */

public class CreateNewActivity extends Activity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback, Response.ErrorListener {

    String[] sportNames = {"Football", "Basketball", "Volleyball", "Jogging", "Gym", "Cycling", "Tennis", "Ping Pong", "Squash", "Others"};
    String[] sportCodes = {"FOT", "BAS", "VOL", "JOG", "GYM", "CYC", "TEN", "PIN", "SQU", "OTH"};
    int sportImages[] = {R.drawable.ic_sport_football, R.drawable.ic_sport_basketball, R.drawable.ic_sport_voleyball, R.drawable.ic_sport_jogging, R.drawable.ic_sport_gym,
            R.drawable.ic_sport_cycling, R.drawable.ic_sport_tennis, R.drawable.ic_sport_pingpong, R.drawable.ic_sport_squash, R.drawable.ic_sport_others};
    String[] privacyNames = {"Public", "Private"};
    int privacyImages[] = {R.drawable.ic_bnav_user_selected, R.drawable.ic_lock};
    TextView privateText;
    RelativeLayout otherSportLayout;
    RelativeLayout dateLayout;
    TextView calendarTextView;
    RelativeLayout timeLayout;
    EditText playersNumber;
    TextView hourTextView;
    ImageView minusButton;
    ImageView plusButton;
    private GoogleMap m_gmap;
    double longitude = 0;
    double latitude = 0;
    RelativeLayout backButton;
    TextView tapHereTextView;
    public static Button createActivityButton;
    CircleImageView invitedFriends0;
    CircleImageView invitedFriends1;
    CircleImageView invitedFriends2;
    public int eventDate;
    public int eventTime;
    TextView friendsNumber;
    ImageView invitedFriends4;
    static public int ASK_COORDS = 1000;
    static public int ASK_COORDS_DONE = 1001;
    static public int ASK_FRIENDS = 500;
    static public int ASK_FRIENDS_DONE = 501;
    public int maxNumberOfParticipants = 9999;
    EditText descriptionEditText;
    TextView locationName;
    TextView adress;
    String sportCode = "FOT";
    public int privacy = 0;
    EditText otherSportName;
    Spinner sportSpinner;
    int numberOfTotalParticipants = 0;
    EventDetails eventDetails;

    public static String getMonth(int month) {
        String date = new DateFormatSymbols().getMonths()[month - 1];
        return date.substring(0, 1).toUpperCase().concat(date.substring(1, 3));
    }

    public boolean checkConstraints() {
        boolean isConstraintChecked = false;
        if (sportSpinner.getSelectedItemPosition() == 9) {
            if (otherSportName.getText().length() == 0) {
                Toast.makeText(getApplicationContext(), "Please enter other sport name!", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        if (calendarTextView.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter a date!", Toast.LENGTH_SHORT).show();
            return true;
        } else
            if (hourTextView.getText().length() == 0) {
                Toast.makeText(getApplicationContext(), "Please enter a time!", Toast.LENGTH_SHORT).show();
                return true;
            } else
                if (GMapsActivity.markerSelected == null) {
                    Toast.makeText(getApplicationContext(), "Please select a location!", Toast.LENGTH_SHORT).show();
                    return true;
                } else
                    if (Integer.valueOf(playersNumber.getText().toString()) < numberOfTotalParticipants + 1) {
                        Toast.makeText(getApplicationContext(), "Too much participants for this capacity!", Toast.LENGTH_SHORT).show();
                        return true;

                    }


        return isConstraintChecked;

    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        setContentView(R.layout.create_new_activity);

        findViewById(R.id.internetRefresh).setAlpha(0);

        final Calendar myCalendar = Calendar.getInstance();
        privateText = (TextView) findViewById(R.id.privateText);
        otherSportLayout = (RelativeLayout) findViewById(R.id.chooseSportNameLayout);
        sportSpinner = (Spinner) findViewById(R.id.sportSpinner);
        dateLayout = (RelativeLayout) findViewById(R.id.dateLayout);
        timeLayout = (RelativeLayout) findViewById(R.id.timeLayout);
        calendarTextView = (TextView) findViewById(R.id.calendarTextView);
        hourTextView = (TextView) findViewById(R.id.hourTextView);
        backButton = (RelativeLayout) findViewById(R.id.backButton);
        minusButton = (ImageView) findViewById(R.id.minusButton);
        plusButton = (ImageView) findViewById(R.id.plusButton);
        playersNumber = (EditText) findViewById(R.id.playersNumber);
        otherSportName = (EditText) findViewById(R.id.otherSportName);
        TextView titleText = (TextView) findViewById(R.id.titleText);
        tapHereTextView = (TextView) findViewById(R.id.tapHereTextView);
        invitedFriends0 = (CircleImageView) findViewById(R.id.invitedFriends0);
        invitedFriends1 = (CircleImageView) findViewById(R.id.invitedFriends1);
        invitedFriends2 = (CircleImageView) findViewById(R.id.invitedFriends2);
        friendsNumber = (TextView) findViewById(R.id.friendsNumber);
        invitedFriends4 = (ImageView) findViewById(R.id.invitedFriends4);
        createActivityButton = (Button) findViewById(R.id.createActivityButton);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        eventDetails = new EventDetails();

        //check if is a Create or Edit


        createActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkConstraints()) {
                    if (!getIntent().getBooleanExtra("isEdit", false)) {
                        Toast.makeText(CreateNewActivity.this, "Creating...", Toast.LENGTH_LONG).show();
                        HashMap<String, String> params = new HashMap<String, String>();
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("authKey", Persistance.getInstance().getUserInfo(CreateNewActivity.this).authKey);
                        params.put("userId", Persistance.getInstance().getUserInfo(CreateNewActivity.this).id);


                        Calendar calendar = myCalendar;
                        myCalendar.getTimeInMillis();
                        params.put("eventDate", String.valueOf(myCalendar.getTimeInMillis() / 1000));
                        params.put("creationDate", String.valueOf(System.currentTimeMillis() / 1000));
                        params.put("description", descriptionEditText.getText().toString());
                        params.put("latitude", String.valueOf(GMapsActivity.markerSelected.getPosition().latitude));
                        params.put("longitude", String.valueOf(GMapsActivity.markerSelected.getPosition().longitude));
                        if (!locationName.getText().toString().equals("Unauthorized location")) {
                            params.put("placeName", locationName.getText().toString());
                        } else {
                            params.put("placeName", adress.getText().toString());
                        }
                        params.put("privacy", String.valueOf(privacy));
                        params.put("capacity", playersNumber.getText().toString());
                        params.put("sportCode", sportCode);
                        if (sportCode.equals("OTH")) {
                            params.put("otherSportName", otherSportName.getText().toString());
                        }
                        params.put("numberOfOffliners", String.valueOf(InviteFriendsActivity.numberOfOfflineFriends));
                        int counterOfInvitedFriends = 0;
                        for (int i = 0; i < InviteFriendsActivity.friendsList.size(); i++) {
                            if (InviteFriendsActivity.friendsList.get(i).isInvited) {
                                params.put("invitedParticipants[" + counterOfInvitedFriends + "]", InviteFriendsActivity.friendsList.get(i).userID);
                                counterOfInvitedFriends++;
                            }
                        }
                        HTTPResponseController.getInstance().createEvent(params, headers, CreateNewActivity.this, null, CreateNewActivity.this);

                    } else {
                        Toast.makeText(CreateNewActivity.this, "Saving...", Toast.LENGTH_LONG).show();
                        HashMap<String, String> params = new HashMap<String, String>();
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("authKey", Persistance.getInstance().getUserInfo(CreateNewActivity.this).authKey);
                        params.put("userId", Persistance.getInstance().getUserInfo(CreateNewActivity.this).id);
                        params.put("eventId", getIntent().getStringExtra("eventId"));


                        Calendar calendar = myCalendar;
                        myCalendar.getTimeInMillis();
                        if (eventDetails.eventDate < myCalendar.getTimeInMillis() / 1000) {
                            params.put("eventDate", String.valueOf(myCalendar.getTimeInMillis() / 1000));
                        }
                        if (!eventDetails.description.equals(descriptionEditText.getText().toString())) {
                            params.put("description", descriptionEditText.getText().toString());
                        }
                        if (eventDetails.latitude != GMapsActivity.markerSelected.getPosition().latitude || eventDetails.longitude != GMapsActivity.markerSelected.getPosition().longitude) {
                            params.put("latitude", String.valueOf(GMapsActivity.markerSelected.getPosition().latitude));
                            params.put("longitude", String.valueOf(GMapsActivity.markerSelected.getPosition().longitude));
                            if (!locationName.getText().toString().equals("Unauthorized location")) {
                                params.put("placeName", locationName.getText().toString());
                            } else {
                                params.put("placeName", adress.getText().toString());
                            }
                        }
                        if (eventDetails.privacy != privacy) {
                            params.put("privacy", String.valueOf(privacy));
                        }
                        if (eventDetails.capacity != Integer.valueOf(playersNumber.getText().toString())) {
                            params.put("capacity", playersNumber.getText().toString());
                        }
                        if (!eventDetails.sportName.equals(sportCode)) {
                            params.put("sportCode", sportCode);
                            if (sportCode.equals("OTH")) {
                                params.put("otherSportName", otherSportName.getText().toString());
                            }
                        }
                        if (InviteFriendsActivity.numberOfOfflineFriends != 0) {
                            params.put("numberOfOffliners", String.valueOf(InviteFriendsActivity.numberOfOfflineFriends));
                        }
                        int counterOfInvitedFriends = 0;
                        if (InviteFriendsActivity.friendsList.size() > 0) {
                            for (int i = 0; i < InviteFriendsActivity.friendsList.size(); i++) {
                                if (InviteFriendsActivity.friendsList.get(i).isInvited) {
                                    params.put("invitedParticipants[" + counterOfInvitedFriends + "]", InviteFriendsActivity.friendsList.get(i).userID);
                                    counterOfInvitedFriends++;
                                }
                            }
                        }
                        HTTPResponseController.getInstance().createEvent(params, headers, CreateNewActivity.this, null, CreateNewActivity.this);
                    }


                    createActivityButton.setEnabled(false);
                }
            }
        });

        invitedFriends0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getIntent().getBooleanExtra("isEdit", false)) {
                    Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                    startActivityForResult(goToNextActivity, ASK_FRIENDS);
                } else {
                    Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                    goToNextActivity.putExtra("isEdit", true);
                    goToNextActivity.putExtra("eventId", getIntent().getStringExtra("eventId"));
                    startActivityForResult(goToNextActivity, ASK_FRIENDS);
                }
            }
        });


        InviteFriendsActivity.friendsList.clear();
        InviteFriendsActivity.numberOfOfflineFriends = 0;
        InviteFriendsActivity.inviteFriendsAdapter = null;
        InviteFriendsActivity.isFirstTimeInviteFriends = false;

        if (!getIntent().getBooleanExtra("isEdit", false)) {
            titleText.setText("Create Activity");
        } else {
            titleText.setText("Edit Activity");
        }
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.ttf");


        sportSpinner.setOnItemSelectedListener(this);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        otherSportName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otherSportName.getText().length() >= 32) {
                    Toast.makeText(getApplicationContext(), "You have reached maximum lenght for this field!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (descriptionEditText.getText().length() >= 100) {
                    Toast.makeText(getApplicationContext(), "You have reached maximum lenght for this field!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        CustomSpinner customAdapterSports = new CustomSpinner(getApplicationContext(), sportImages, sportNames);
        sportSpinner.setAdapter(customAdapterSports);
        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (sportSpinner.getSelectedItemPosition() == 9) {
                    ViewGroup.LayoutParams params = otherSportLayout.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    otherSportLayout.setLayoutParams(params);
                    sportCode = sportCodes[position];
                } else {
                    ViewGroup.LayoutParams params = otherSportLayout.getLayoutParams();
                    params.height = 0;
                    otherSportLayout.setLayoutParams(params);
                    sportCode = sportCodes[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final Spinner privacySpinner = (Spinner) findViewById(R.id.privacySpinner);
        privacySpinner.setOnItemSelectedListener(this);
        playersNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                playersNumber.setSelection(playersNumber.getText().length());

                return false;
            }
        });
        CustomSpinner customAdapterPrivacy = new CustomSpinner(getApplicationContext(), privacyImages, privacyNames);
        privacySpinner.setAdapter(customAdapterPrivacy);
        privacySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (privacySpinner.getSelectedItemPosition() == 1) {
                    ViewGroup.LayoutParams params = privateText.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    privateText.setLayoutParams(params);
                    ViewGroup.MarginLayoutParams margins = (ViewGroup.MarginLayoutParams) privateText.getLayoutParams();
                    margins.topMargin = 16;
                    privateText.setLayoutParams(margins);
                    privacy = 1;
                } else {
                    ViewGroup.LayoutParams params = privateText.getLayoutParams();
                    params.height = 0;
                    privateText.setLayoutParams(params);
                    ViewGroup.MarginLayoutParams margins = (ViewGroup.MarginLayoutParams) privateText.getLayoutParams();
                    margins.topMargin = 0;
                    privateText.setLayoutParams(margins);
                    privacy = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                try {
                    myCalendar.getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String displayDate = formatter.format(myCalendar.getTime());
                    String[] stringDate = displayDate.split("-");
                    String date = stringDate[2] + " " + getMonth(Integer.parseInt(stringDate[1])) + " " + stringDate[0];
                    calendarTextView.setText(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };


        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                // TODO Auto-generated method stub
                DatePickerDialog dpd = new DatePickerDialog(CreateNewActivity.this, R.style.DialogTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        timeLayout.setOnClickListener(new View.OnClickListener() {

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
                        hourTextView.setText((selectedHour < 9 ? "0" : "") + selectedHour + ":" + (selectedMinute < 9 ? "0" : "") + selectedMinute);
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendar.set(Calendar.MINUTE, selectedMinute);

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!playersNumber.getText().toString().equals("2")) {
                    playersNumber.setText(String.valueOf(Integer.valueOf(playersNumber.getText().toString()) - 1));
                    playersNumber.setSelection(playersNumber.getText().length());
                }
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!playersNumber.getText().toString().equals(String.valueOf(maxNumberOfParticipants))) {
                    playersNumber.setText(String.valueOf(Integer.valueOf(playersNumber.getText().toString()) + 1));
                    playersNumber.setSelection(playersNumber.getText().length());
                }
            }
        });

        if (getIntent().getBooleanExtra("isEdit", false)) {
            Bundle b = getIntent().getExtras();

            eventDetails.eventDate = b.getInt("eventDate");
            eventDetails.description = b.getString("description");
            eventDetails.placeName = b.getString("placeName");
            eventDetails.latitude = b.getDouble("latitude");
            eventDetails.longitude = b.getDouble("longitude");
            eventDetails.placeId = b.getString("placeId");
            eventDetails.isAuthorized = b.getInt("isAuthorized");
            eventDetails.placeAdress = b.getString("placeAdress");
            eventDetails.authorizeLevel = b.getString("authorizelevel");
            eventDetails.companyImage = b.getString("placeImage");
            eventDetails.sportName = b.getString("sportName");
            eventDetails.otherSportName = b.getString("otherSportName");
            eventDetails.capacity = b.getInt("capacity");
            eventDetails.numberOfParticipants = b.getInt("numberOfParticipants");
            eventDetails.points = b.getInt("points");
            eventDetails.isParticipant = b.getInt("isParticipant");
            eventDetails.ludicoins = b.getInt("ludicoins");
            eventDetails.privacy = b.getInt("privacy");
            eventDetails.creatorName = b.getString("creatorName");
            eventDetails.creatorLevel = b.getInt("creatorLevel");
            eventDetails.creatorId = b.getString("creatorId");
            eventDetails.creatorProfilePicture = b.getString("creatorProfilePicture");
            for (int i = 0; i < b.getStringArrayList("participantsId").size(); i++) {
                Friend friend = new Friend();
                friend.userID = b.getStringArrayList("participantsId").get(i);
                friend.userName = b.getStringArrayList("participantsName").get(i);
                friend.profileImage = b.getStringArrayList("participantsProfileImage").get(i);
                friend.level = b.getIntegerArrayList("participantsLevel").get(i);
                eventDetails.listOfParticipants.add(friend);
            }

            createActivityButton.setText("SAVE CHANGES");

            //set custom data for edit

            switch (eventDetails.sportName) {
            case "FOT":
                sportSpinner.setSelection(0);
                break;
            case "BAS":
                sportSpinner.setSelection(1);
                break;
            case "VOL":
                sportSpinner.setSelection(2);
                break;
            case "JOG":
                sportSpinner.setSelection(3);
                break;
            case "GYM":
                sportSpinner.setSelection(4);
                break;
            case "CYC":
                sportSpinner.setSelection(5);
                break;
            case "TEN":
                sportSpinner.setSelection(6);
                break;
            case "PIN":
                sportSpinner.setSelection(7);
                break;
            case "SQU":
                sportSpinner.setSelection(8);
                break;
            case "OTH":
                sportSpinner.setSelection(9);
                otherSportName.setText(eventDetails.otherSportName);
                otherSportName.setSelection(eventDetails.otherSportName.length());
                break;
            }

            if (eventDetails.privacy == 0) {
                privacySpinner.setSelection(0);
            } else {
                privacySpinner.setSelection(1);
            }
            playersNumber.setText(String.valueOf(eventDetails.capacity));

            Calendar editCalendar = Calendar.getInstance();
            ;
            editCalendar.setTimeInMillis((long) eventDetails.eventDate * 1000);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String displayDate = formatter.format(editCalendar.getTime());
            String[] stringDate = displayDate.split("-");
            String data = stringDate[2] + " " + getMonth(Integer.parseInt(stringDate[1])) + " " + stringDate[0];
            calendarTextView.setText(data);

            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            String displayTime = format.format(editCalendar.getTime());
            hourTextView.setText(displayTime);


            View selected_location_layout = findViewById(R.id.root);
            ViewGroup.LayoutParams params = selected_location_layout.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            selected_location_layout.setLayoutParams(params);


            ImageView companyImage = (ImageView) findViewById(R.id.companyImage);

            if (!eventDetails.companyImage.equals("")) {
                Bitmap bitmap = decodeBase64(eventDetails.companyImage);
                companyImage.setImageBitmap(bitmap);
            }
            locationName = (TextView) findViewById(R.id.locationName);
            locationName.setText(eventDetails.placeName);

            adress = (TextView) findViewById(R.id.adress);
            adress.setText(eventDetails.placeAdress);

            TextView ludicoinsNumber = (TextView) findViewById(R.id.ludicoinsNumber);
            TextView pointsNumber = (TextView) findViewById(R.id.pointsNumber);

            if (!eventDetails.authorizeLevel.equals("")) {
                ludicoinsNumber.setText("+" + String.valueOf(eventDetails.ludicoins));
                pointsNumber.setText("+" + String.valueOf(eventDetails.points));
            } else {
                ludicoinsNumber.setText("?");
                pointsNumber.setText("?");
            }

            descriptionEditText.setText(eventDetails.description);
            int countOfFriends = 0;

            for (int i = 0; i < eventDetails.listOfParticipants.size(); i++) {

                if (countOfFriends == 0) {
                    if (!eventDetails.listOfParticipants.get(i).profileImage.equals("")) {
                        Bitmap bitmap = decodeBase64(eventDetails.listOfParticipants.get(i).profileImage);
                        invitedFriends0.setImageBitmap(bitmap);
                    }
                    countOfFriends++;
                    invitedFriends0.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                    invitedFriends1.setVisibility(View.VISIBLE);
                    invitedFriends1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                            goToNextActivity.putExtra("isEdit", true);
                            goToNextActivity.putExtra("eventId", getIntent().getStringExtra("eventId"));
                            startActivityForResult(goToNextActivity, ASK_FRIENDS);
                        }
                    });
                } else
                    if (countOfFriends == 1) {
                        if (!eventDetails.listOfParticipants.get(i).profileImage.equals("")) {
                            Bitmap bitmap = decodeBase64(eventDetails.listOfParticipants.get(i).profileImage);

                            invitedFriends1.setImageBitmap(bitmap);
                        }
                        countOfFriends++;
                        invitedFriends1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                        invitedFriends2.setVisibility(View.VISIBLE);
                        invitedFriends2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                                goToNextActivity.putExtra("isEdit", true);
                                goToNextActivity.putExtra("eventId", getIntent().getStringExtra("eventId"));
                                startActivityForResult(goToNextActivity, ASK_FRIENDS);
                            }
                        });
                    } else
                        if (countOfFriends == 2) {
                            if (!eventDetails.listOfParticipants.get(i).profileImage.equals("")) {
                                Bitmap bitmap = decodeBase64(eventDetails.listOfParticipants.get(i).profileImage);

                                invitedFriends2.setImageBitmap(bitmap);
                            }
                            countOfFriends++;
                            invitedFriends2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });
                            friendsNumber.setVisibility(View.VISIBLE);
                            friendsNumber.setText("");
                            friendsNumber.setBackgroundResource(R.drawable.ic_invite);
                            friendsNumber.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                                    goToNextActivity.putExtra("isEdit", true);
                                    goToNextActivity.putExtra("eventId", getIntent().getStringExtra("eventId"));
                                    startActivityForResult(goToNextActivity, ASK_FRIENDS);
                                }
                            });

                        } else
                            if (countOfFriends > 2) {
                                friendsNumber.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                });
                                countOfFriends++;
                                invitedFriends4.setVisibility(View.VISIBLE);
                                invitedFriends4.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                                        goToNextActivity.putExtra("isEdit", true);
                                        goToNextActivity.putExtra("eventId", getIntent().getStringExtra("eventId"));
                                        startActivityForResult(goToNextActivity, ASK_FRIENDS);
                                    }
                                });


                            }
            }

            numberOfTotalParticipants = eventDetails.numberOfParticipants - 1 - eventDetails.listOfParticipants.size() + countOfFriends;

            if (numberOfTotalParticipants >= 1) {
                invitedFriends0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                invitedFriends1.setVisibility(View.VISIBLE);
                invitedFriends1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                        goToNextActivity.putExtra("isEdit", true);
                        goToNextActivity.putExtra("eventId", getIntent().getStringExtra("eventId"));
                        startActivityForResult(goToNextActivity, ASK_FRIENDS);
                    }
                });
            }
            if (numberOfTotalParticipants >= 2) {
                invitedFriends1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                invitedFriends2.setVisibility(View.VISIBLE);
                invitedFriends2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                        goToNextActivity.putExtra("isEdit", true);
                        goToNextActivity.putExtra("eventId", getIntent().getStringExtra("eventId"));
                        startActivityForResult(goToNextActivity, ASK_FRIENDS);
                    }
                });

            }
            if (numberOfTotalParticipants >= 3) {
                invitedFriends2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                friendsNumber.setVisibility(View.VISIBLE);
                friendsNumber.setText("");
                friendsNumber.setBackgroundResource(R.drawable.ic_invite);
                friendsNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                        goToNextActivity.putExtra("isEdit", true);
                        goToNextActivity.putExtra("eventId", getIntent().getStringExtra("eventId"));
                        startActivityForResult(goToNextActivity, ASK_FRIENDS);
                    }
                });

            }
            if (numberOfTotalParticipants >= 4) {
                friendsNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                friendsNumber.setText("+" + String.valueOf(numberOfTotalParticipants - 3));
                friendsNumber.setBackgroundResource(R.drawable.round_textview);
                invitedFriends4.setVisibility(View.VISIBLE);
                invitedFriends4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                        goToNextActivity.putExtra("isEdit", true);
                        goToNextActivity.putExtra("eventId", getIntent().getStringExtra("eventId"));
                        startActivityForResult(goToNextActivity, ASK_FRIENDS);
                    }
                });


            }


        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_gmap = googleMap;
        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        GPSTracker gps = new GPSTracker(getApplicationContext(), CreateNewActivity.this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            gps.stopUsingGPS();
        }


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (!getIntent().getBooleanExtra("isEdit", false)) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
                } else {

                    tapHereTextView.setText("");
                    LatLng latLng = new LatLng(eventDetails.latitude, eventDetails.longitude);


                    if (!eventDetails.authorizeLevel.equals("")) {

                        switch (Integer.valueOf(eventDetails.authorizeLevel)) {

                        case 0:
                            GMapsActivity.markerSelected = m_gmap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1_selected)));
                            break;
                        case 1:
                            GMapsActivity.markerSelected = m_gmap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_2_selected)));
                            break;
                        case 2:
                            GMapsActivity.markerSelected = m_gmap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_3_selected)));
                            break;

                        case 3:
                            GMapsActivity.markerSelected = m_gmap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_4_selected)));
                            break;


                        }
                    } else {
                        GMapsActivity.markerSelected = m_gmap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1_selected)));

                    }
                    m_gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }


                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (!getIntent().getBooleanExtra("isEdit", false)) {
                            Intent goToNextActivity = new Intent(CreateNewActivity.this, GMapsActivity.class);
                            goToNextActivity.putExtra("latitude", String.valueOf(latitude));
                            goToNextActivity.putExtra("longitude", String.valueOf(longitude));
                            goToNextActivity.putExtra("sportCode", sportCode);
                            startActivityForResult(goToNextActivity, ASK_COORDS);
                        } else {
                            Intent goToNextActivity = new Intent(CreateNewActivity.this, GMapsActivity.class);
                            goToNextActivity.putExtra("latitude", String.valueOf(eventDetails.latitude));
                            goToNextActivity.putExtra("longitude", String.valueOf(eventDetails.longitude));
                            goToNextActivity.putExtra("sportCode", sportCode);
                            startActivityForResult(goToNextActivity, ASK_COORDS);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CreateNewActivity.ASK_COORDS_DONE) {
            Double lat = 0.0;
            Double lng = 0.0;
            String addressName = "";
            String placeName = "";
            String image = "";
            int ludicoins = 0;
            int points = 0;
            int authorizeEventLevel = -1;
            tapHereTextView.setText("");
            if (data != null) {
                lat = data.getDoubleExtra("latitude", 0);
                lng = data.getDoubleExtra("longitude", 0);
                authorizeEventLevel = data.getIntExtra("AuthorizeEventLevel", -2);
                addressName = data.getStringExtra("address");
                placeName = data.getStringExtra("placeName");
                if (authorizeEventLevel != -1) {
                    image = data.getStringExtra("image");
                    ludicoins = data.getIntExtra("ludicoins", 0);
                    points = data.getIntExtra("points", 0);
                }


            }

            View selected_location_layout = findViewById(R.id.root);
            ViewGroup.LayoutParams params = selected_location_layout.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            selected_location_layout.setLayoutParams(params);


            ImageView companyImage = (ImageView) findViewById(R.id.companyImage);

            if (!image.equals("")) {
                Bitmap bitmap = decodeBase64(image);
                companyImage.setImageBitmap(bitmap);
            }
            locationName = (TextView) findViewById(R.id.locationName);
            locationName.setText(placeName);

            adress = (TextView) findViewById(R.id.adress);
            adress.setText(addressName);

            TextView ludicoinsNumber = (TextView) findViewById(R.id.ludicoinsNumber);
            TextView pointsNumber = (TextView) findViewById(R.id.pointsNumber);

            if (authorizeEventLevel != -1) {
                ludicoinsNumber.setText(String.valueOf(ludicoins));
                pointsNumber.setText(String.valueOf(points));
            } else {
                ludicoinsNumber.setText(String.valueOf("?"));
                pointsNumber.setText(String.valueOf("?"));

                HashMap<String, String> header = new HashMap<>();
                header.put("authKey", Persistance.getInstance().getUserInfo(this).authKey);
                HTTPResponseController.getInstance().valuesForUnauthorized(header, "sportCode=" + sportCode, this, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        onNotAutorizedCheck(response);
                    }
                }, this);
            }

            LatLng latLng = new LatLng(lat, lng);

            m_gmap.clear();

            switch (authorizeEventLevel) {
            case -1:
                m_gmap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1_selected)));
                break;
            case 0:
                m_gmap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1_selected)));
                break;
            case 1:
                m_gmap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_2_selected)));
                break;
            case 2:
                m_gmap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_3_selected)));
                break;

            case 3:
                m_gmap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_4_selected)));
                break;


            }
            m_gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } else
            if (resultCode == CreateNewActivity.ASK_FRIENDS_DONE) {
                int countOfFriends = 0;
                invitedFriends0.setImageResource(R.drawable.ic_invite);
                invitedFriends1.setImageResource(R.drawable.ic_invite);
                invitedFriends2.setImageResource(R.drawable.ic_invite);
                friendsNumber.setBackgroundResource(R.drawable.ic_invite);
                invitedFriends4.setImageResource(R.drawable.ic_invite);
                invitedFriends1.setVisibility(View.INVISIBLE);
                invitedFriends2.setVisibility(View.INVISIBLE);
                friendsNumber.setVisibility(View.INVISIBLE);
                invitedFriends4.setVisibility(View.INVISIBLE);
                invitedFriends0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                        startActivityForResult(goToNextActivity, ASK_FRIENDS);
                    }
                });
                invitedFriends1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                invitedFriends2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                friendsNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                invitedFriends4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                for (int i = 0; i < InviteFriendsActivity.friendsList.size(); i++) {


                    if (InviteFriendsActivity.friendsList.get(i).isInvited && countOfFriends == 0) {
                        if (!InviteFriendsActivity.friendsList.get(i).profileImage.equals("")) {
                            Bitmap bitmap = decodeBase64(InviteFriendsActivity.friendsList.get(i).profileImage);
                            invitedFriends0.setImageBitmap(bitmap);
                        }
                        countOfFriends++;
                        invitedFriends0.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });

                        invitedFriends1.setVisibility(View.VISIBLE);
                        invitedFriends1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                                startActivityForResult(goToNextActivity, ASK_FRIENDS);
                            }
                        });
                    } else
                        if (InviteFriendsActivity.friendsList.get(i).isInvited && countOfFriends == 1) {
                            if (!InviteFriendsActivity.friendsList.get(i).profileImage.equals("")) {
                                Bitmap bitmap = decodeBase64(InviteFriendsActivity.friendsList.get(i).profileImage);

                                invitedFriends1.setImageBitmap(bitmap);
                            }
                            countOfFriends++;
                            invitedFriends1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });
                            invitedFriends2.setVisibility(View.VISIBLE);
                            invitedFriends2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                                    startActivityForResult(goToNextActivity, ASK_FRIENDS);
                                }
                            });
                        } else
                            if (InviteFriendsActivity.friendsList.get(i).isInvited && countOfFriends == 2) {
                                if (!InviteFriendsActivity.friendsList.get(i).profileImage.equals("")) {
                                    Bitmap bitmap = decodeBase64(InviteFriendsActivity.friendsList.get(i).profileImage);

                                    invitedFriends2.setImageBitmap(bitmap);
                                }
                                countOfFriends++;
                                invitedFriends2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });
                                friendsNumber.setVisibility(View.VISIBLE);
                                friendsNumber.setText("");
                                friendsNumber.setBackgroundResource(R.drawable.ic_invite);
                                friendsNumber.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                                        startActivityForResult(goToNextActivity, ASK_FRIENDS);
                                    }
                                });

                            } else
                                if (InviteFriendsActivity.friendsList.get(i).isInvited && countOfFriends > 2) {
                                    friendsNumber.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                        }
                                    });
                                    countOfFriends++;
                                    invitedFriends4.setVisibility(View.VISIBLE);
                                    invitedFriends4.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                                            startActivityForResult(goToNextActivity, ASK_FRIENDS);
                                        }
                                    });

                                }
                }

                numberOfTotalParticipants = numberOfOfflineFriends + countOfFriends;

                if (numberOfOfflineFriends + countOfFriends >= 1) {
                    invitedFriends0.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
                    invitedFriends1.setVisibility(View.VISIBLE);
                    invitedFriends1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                            startActivityForResult(goToNextActivity, ASK_FRIENDS);
                        }
                    });
                }
                if (numberOfOfflineFriends + countOfFriends >= 2) {
                    invitedFriends1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    invitedFriends2.setVisibility(View.VISIBLE);
                    invitedFriends2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                            startActivityForResult(goToNextActivity, ASK_FRIENDS);
                        }
                    });
                }
                if (numberOfOfflineFriends + countOfFriends >= 3) {
                    invitedFriends2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    friendsNumber.setVisibility(View.VISIBLE);
                    friendsNumber.setText("");
                    friendsNumber.setBackgroundResource(R.drawable.ic_invite);
                    friendsNumber.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                            startActivityForResult(goToNextActivity, ASK_FRIENDS);
                        }
                    });
                }
                if (numberOfOfflineFriends + countOfFriends >= 4) {
                    friendsNumber.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    friendsNumber.setText("+" + String.valueOf(numberOfOfflineFriends + countOfFriends - 3));
                    friendsNumber.setBackgroundResource(R.drawable.round_textview);
                    invitedFriends4.setVisibility(View.VISIBLE);
                    invitedFriends4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent goToNextActivity = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                            startActivityForResult(goToNextActivity, ASK_FRIENDS);
                        }
                    });

                }
            }
    }

    private void onNotAutorizedCheck(JSONObject response) {
        try {
            TextView ludicoinsNumber = (TextView) findViewById(R.id.ludicoinsNumber);
            TextView pointsNumber = (TextView) findViewById(R.id.pointsNumber);

            ludicoinsNumber.setText("+" + response.getInt("ludicoins"));
            pointsNumber.setText("+" + response.getInt("points"));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        if (error instanceof NetworkError) {
            RelativeLayout ll = (RelativeLayout) findViewById(R.id.noInternetLayout);
            final float scale = super.getResources().getDisplayMetrics().density;
            int pixels = (int) (56 * scale + 0.5f);
            ll.getLayoutParams().height = pixels;
            ll.setLayoutParams(ll.getLayoutParams());
        }
    }
}



